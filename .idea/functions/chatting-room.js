/**
 * 채팅방 관리와 관련된 Firebase Cloud Functions를 모아둔 파일입니다.
 */
const {onDocumentCreated, onDocumentUpdated} =
    require("firebase-functions/v2/firestore");
const logger = require("firebase-functions/logger");
const admin = require("firebase-admin");

/**
 * 'ChattingRooms' 컬렉션에 새로운 문서가 생성될 때마다 트리거되는 함수입니다.
 * - 채팅방에 참여하는 각 사용자의 'users' 문서에 채팅방 ID를 추가합니다.
 * - 채팅 메시지를 저장하기 위한 'Chattings' 문서를 생성합니다.
 */
exports.handleNewChattingRoom = onDocumentCreated(
    "chattingRooms/{chattingRoomId}", async (event) => {
      const snapshot = event.data;
      if (!snapshot) {
        logger.error("No data associated with the event.");
        return;
      }

      const newChattingRoomData = snapshot.data();
      const chattingRoomId = event.params.chattingRoomId;
      const users = newChattingRoomData.users;

      logger.info(`New chatting room created with ID: ${chattingRoomId}`);
      logger.info("Users in the room:", users);

      if (!users || !Array.isArray(users) || users.length === 0) {
        logger.error("Chatting room creation event has no users.");
        return null;
      }

      const firestore = admin.firestore();
      const batch = firestore.batch();

      const creator = users[0];
      const otherUsers = users.slice(1);
      const invitedUserIds = otherUsers.map((user) => user.userId);

      // 1. ChattingRooms의 `users` 필드에 있는 각 사용자의 문서를 업데이트합니다.
      // `users` 배열에 `userId` 필드가 포함되어 있다고 가정합니다.
      for (const user of users) {
        const userId = user.userId;
        const userDocRef = firestore.collection("users").doc(userId);
        batch.update(userDocRef, {
          chattingRooms: admin.firestore.FieldValue
              .arrayUnion(chattingRoomId),
        });
      }

      // 2. 'Chattings' 컬렉션에 채팅 메시지를 저장할 문서를 생성합니다.
      const chattingsDocRef =
          firestore.collection("chattings").doc(chattingRoomId);
      const userNickNames = users.map((x) => x.nickName);

      // 초기 채팅방 생성 메시지를 설정합니다.
      const initialChattingMessage = {
        chatSeq: 1,
        userId: creator.userId,
        profileUrl: newChattingRoomData.lstProfileUrl || creator.profileUrl,
        nickName: creator.nickName,
        msg: newChattingRoomData.lstMsg ||
          `${creator.nickName}님이 ${userNickNames.join(",")}을 초대하였습니다.`,
        type: "in",
        invitedUsers: invitedUserIds,
        fileUrls: [],
        unreadUsers: invitedUserIds.filter((x) => x !== creator),
        sendDate: newChattingRoomData.lstSendDate,
      };

      batch.set(chattingsDocRef, {
        chattings: [initialChattingMessage], // 초기 메시지로 배열을 초기화합니다.
      });

      try {
        await batch.commit();
        logger.info(`chatting room creation for ${chattingRoomId}`);
      } catch (error) {
        logger.error(
            `Error handling new chatting room ${chattingRoomId}:`,
            error,
        );
      }

      return null;
    },
);

/**
 * 'chattings' 컬렉션의 문서가 업데이트될 때 트리거되는 함수.
 * 'chattingRoom' 컬렉션의 문서 필드(lstProfileUrl, lstMsg, lstSendDate)와
 * users 배열 내의 unreadCnt를 업데이트합니다.
 */
exports.updateChattingRoomLastMessage = onDocumentUpdated(
    "chattings/{uid}",
    async (event) => {
      const db = admin.firestore();

      const afterData = event.data.after.data();
      const beforeData = event.data.before.data();

      // 메시지 배열에 변경이 없는 경우 함수 종료
      const beforeChattings = (beforeData && beforeData.chattings) ?
        beforeData.chattings :
        [];
      const afterChattings = (afterData && afterData.chattings) ?
        afterData.chattings :
        [];

      if (JSON.stringify(beforeChattings) === JSON.stringify(afterChattings)) {
        logger.info("메시지 배열에 변경이 없습니다. 업데이트를 건너갑니다.");
        return null;
      }

      if (
        !afterData ||
        !afterData.chattings ||
        afterData.chattings.length === 0) {
        logger.info("업데이트된 데이터가 없거나, 메시지 배열이 비어있습니다.");
        return null;
      }

      const uid = event.params.uid;
      const lastMessageIndex = afterData.chattings.length - 1;
      const lastMessage = afterData.chattings[lastMessageIndex];

      // 마지막 메시지 필드 추출
      const {profileUrl, msg, sendDate} = lastMessage;

      // 마지막 메시지의 userId와 동일한 chattings 필드 내의 모든 메시지를 필터링
      const unreadUsers = afterData.chattings.flatMap(
          (chattingMsg) => chattingMsg.unreadUsers,
      );

      // 'chattingRoom' 컬렉션의 해당 문서 참조
      const chattingRoomRef = db.collection("chattingRooms").doc(uid);

      try {
        // 트랜잭션을 사용하여 읽기-수정-쓰기 작업을 원자적으로 처리
        await db.runTransaction(async (transaction) => {
          const chattingRoomDoc = await transaction.get(chattingRoomRef);

          if (!chattingRoomDoc.exists) {
            throw new Error("ChattingRoom 문서가 존재하지 않습니다.");
          }

          const currentChattingRoomData = chattingRoomDoc.data();
          // 옵셔널 체이닝 대신 전통적인 if-else 또는 논리 연산자 사용
          const currentUsers = (
            currentChattingRoomData && currentChattingRoomData.users
            ) ?
            currentChattingRoomData.users :
            [];

          const updatedUsers = currentUsers.map((user) => {
            // 마지막 메시지를 보낸 userId와 일치하는 사용자의 unreadCnt를 unreadCount로 설정
            return {
              ...user,
              unreadCnt: unreadUsers.filter((x) => x === user.userId).length,
            };
          });

          // Firestore 문서 업데이트
          transaction.update(chattingRoomRef, {
            lstProfileUrl: profileUrl,
            lstMsg: msg,
            lstSendDate: sendDate,
            users: updatedUsers,
          });
        });

        logger.info(`채팅방(${uid}) 문서가 성공적으로 업데이트되었습니다.`);
        return null;
      } catch (error) {
        logger.error("채팅방 문서 업데이트 중 오류 발생:", error);
        return null;
      }
    },
);

/**
 * 'chattings' 컬렉션의 문서에서 'onUsers' 필드가 업데이트될 때 트리거되는 함수.
 * chattings 배열 내의 메시지들의 unreadUsers를 업데이트합니다.
 */
exports.updateUnreadStatusOnUserChange = onDocumentUpdated(
    "chattings/{uid}",
    async (event) => {
      const db = admin.firestore();
      const beforeData = event.data.before.data();
      const afterData = event.data.after.data();

      const beforeOnUsers = beforeData.onUsers || [];
      const afterOnUsers = afterData.onUsers || [];

      // onUsers 필드가 변경되었고 chattings 배열은 변경되지 않았을 때만 실행
      if (
        JSON.stringify(beforeOnUsers) !== JSON.stringify(afterOnUsers) &&
        beforeData.chattings.length === afterData.chattings.length
      ) {
        logger.info("onUsers 필드 변경 감지. 읽음 상태 업데이트 로직 실행.");

        const batch = db.batch();
        const updatedChattings = afterData.chattings.map((chattingMsg) => {
          const updatedUnreadUsers = chattingMsg.unreadUsers.filter(
              (userId) => !afterOnUsers.includes(userId),
          );
          return {...chattingMsg, unreadUsers: updatedUnreadUsers};
        });

        const chattingsRef = db.collection("chattings").doc(event.params.uid);
        batch.update(chattingsRef, {chattings: updatedChattings});

        try {
          await batch.commit();
          logger.info(`채팅(${event.params.uid}) 문서의 읽음 상태가 업데이트되었습니다.`);
        } catch (error) {
          logger.error(
              `채팅 문서 업데이트 중 오류 발생(${event.params.uid}):`,
              error,
          );
        }
        return null;
      }
      return null;
    },
);
