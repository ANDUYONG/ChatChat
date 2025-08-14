/**
 * 사용자 관리와 관련된 Firebase Cloud Functions를 모아둔 파일입니다.
 */
const {onDocumentCreated, onDocumentUpdated} =
    require("firebase-functions/v2/firestore");
const logger = require("firebase-functions/logger");
const admin = require("firebase-admin");

/**
 * 'users' 컬렉션에 새로운 문서가 생성될 때마다 트리거되는 함수입니다.
 * 생성된 사용자의 기본 정보를 'profiles' 컬렉션에 복사하여 프로필 문서를 생성합니다.
 */
exports.createProfileForNewUser = onDocumentCreated(
    "users/{userId}", async (event) => {
      const snapshot = event.data;
      if (!snapshot) {
        logger.error("No data associated with the event.");
        return;
      }

      const newUser = snapshot.data();
      const userId = event.params.userId;

      logger.info(`New user created with ID: ${userId}`, newUser);

      const profileData = {
        userId: userId,
        email: newUser.email || null,
        name: newUser.name || "새 사용자",
        nickName: newUser.nickName || "새 사용자",
        statusMsg: "",
        profileUrl: "",
        backgroundUrls: [],
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
      };

      try {
        await admin.firestore()
            .collection("profiles")
            .doc(userId).set(profileData);
        logger.info(`Profile successfully created for user: ${userId}`);
        return null;
      } catch (error) {
        logger.error(
            `Error creating profile for user ${userId}:`,
            error,
        );
        return null;
      }
    });

/**
 * 'users' 컬렉션의 문서가 업데이트될 때마다 트리거되는 함수입니다.
 * profileUrl 또는 backgroundUrls가 변경되면 'profiles' 컬렉션에 해당 필드를 업데이트합니다.
 */
exports.updateProfileFromUser = onDocumentUpdated(
    "users/{userId}", async (event) => {
      const change = event.data;
      if (!change) {
        logger.error("No data associated with the event.");
        return;
      }

      const userId = event.params.userId;
      const beforeData = change.before.data();
      const afterData = change.after.data();

      logger.info(`User document updated for ID: ${userId}`);

      const updateData = {};

      if (beforeData.profileUrl !== afterData.profileUrl) {
        logger.info(`profileUrl changed for user ${userId}.`);
        updateData.profileUrl = afterData.profileUrl;
      }

      if (JSON.stringify(beforeData.backgroundUrls) !==
          JSON.stringify(afterData.backgroundUrls)) {
        logger.info(`backgroundUrls changed for user ${userId}.`);
        updateData.backgroundUrls = afterData.backgroundUrls;
      }

      if (Object.keys(updateData).length > 0) {
        try {
          await admin.firestore()
              .collection("profiles")
              .doc(userId)
              .update(updateData);
          logger.info(`for user ${userId}.`, updateData);
        } catch (error) {
          logger.error(
              `Error for user ${userId}:`,
              error,
          );
        }
      } else {
        logger.info(`${userId}. No update needed.`);
      }

      return null;
    });


exports.updateProfileFromUser = onDocumentUpdated(
    "users/{userId}",
    async (event) => {
      const userId = event.params.userId;
      const beforeData = event.data.before.data();
      const afterData = event.data.after.data();

      const beforeChattingRooms = beforeData.chattingRooms || [];
      const afterChattingRooms = afterData.chattingRooms || [];

      // 1. Find the deleted chattingRooms field values
      const removedRoomIds = beforeChattingRooms.filter(
          (roomId) => !afterChattingRooms.includes(roomId),
      );

      if (removedRoomIds.length === 0) {
        console.log("No value was deleted");
        return null;
      }

      const promises = removedRoomIds.map(async (roomId) => {
        const chattingRoomRef = admin
            .firestore()
            .collection("chattingRooms")
            .doc(roomId);
        const chattingsRef = admin
            .firestore()
            .collection("chattings")
            .doc(roomId);

        // 2. Update the users field in the chattingRooms collection
        await admin.firestore().runTransaction(async (transaction) => {
          const chattingRoomDoc = await transaction.get(chattingRoomRef);
          if (!chattingRoomDoc.exists) {
            console.error(`chattingRooms/${roomId} document does not exist.`);
            return;
          }

          const roomData = chattingRoomDoc.data();
          const users = roomData.users || [];

          // Change the 'exist' value to 'false'
          const updatedUsers = users.map((user) => {
            if (user.userId === userId) {
              return {...user, exist: false};
            }
            return user;
          });

          transaction.update(chattingRoomRef, {users: updatedUsers});

          // 사용자가 나갔으므로 onUsers 배열에서도 해당 사용자를 제거합니다.
          const onUsers = chattingsRef.onUsers;
          const updatedOnUsers = onUsers && onUsers.length > 0 ?
              onUsers.filter((uid) => uid !== userId) : [];

          // 3. Add an "exit" message to the chattings collection
          const afterUserDoc = await admin
              .firestore()
              .collection("users")
              .doc(userId)
              .get();
          const afterUserData = afterUserDoc.data();
          const nickName = afterUserData.nickName || "Unknown User";
          const profileUrl = afterUserData.profileUrl || "";

          const newChattingMsg = {
            userId: userId,
            profileUrl: profileUrl,
            nickName: nickName,
            type: "out",
            msg: `${nickName}님이 방을 나가셨습니다.`,
            sendDate: new Date(Date.now()),
          };

          // 4. Calculate and add the unreadUsers field
          // 최신 업데이트된 users 배열인 updatedUsers를 사용
          const unreadUsers = updatedUsers
              .filter((user) =>
                user.exist === true && updatedOnUsers.includes(user.userId))
              .map((user) => user.userId);

          newChattingMsg.unreadUsers = unreadUsers;
          // onUsers 필드도 업데이트된 배열로 변경
          transaction.update(chattingsRef, {
            chattings: admin.firestore.FieldValue.arrayUnion(newChattingMsg),
          });

          // chattingRooms 컬렉션의 onUsers 필드도 업데이트
          transaction.update(chattingRoomRef, {onUsers: updatedOnUsers});
        });
      });

      await Promise.all(promises);

      console.log("All updates have been completed successfully.");
      return null;
    },
);
