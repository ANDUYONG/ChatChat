/**
 * 채팅방 알림과 관련된 Firebase Cloud Functions를 모아둔 파일입니다.
 * package.json에 "firebase-functions"와 "firebase-admin"이 설치되어 있어야 합니다.
 */

// Cloud Functions와 Admin SDK를 가져옵니다.
const {onDocumentUpdated} = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");
const db = admin.firestore();

/**
 * "chattings" 컬렉션의 문서가 업데이트될 때 실행되는 트리거 함수입니다.
 * - 새로운 메시지가 "chattings" 필드에 추가되면 푸시 알림을 보냅니다.
 * - 이 함수를 사용하기 전에 Firebase 프로젝트에 FCM(Firebase Cloud Messaging)이 활성화되어 있어야 합니다.
 * @param {functions.firestore.DocumentSnapshot} change 업데이트 전후의 문서 스냅샷
 * @param {functions.EventContext} context 이벤트 컨텍스트 (문서 ID 등 포함)
 */
exports.onUpdateChattingContent = onDocumentUpdated(
    "chattings/{roomId}",
    async (event) => {
      // 문서의 이전 상태와 현재 상태를 가져옵니다.
      const beforeData = event.data.before.data();
      const afterData = event.data.after.data();
      const roomId = event.params.roomId;

      // chattings 필드의 길이가 변경되었는지 확인하여 새로운 메시지가 추가되었는지 판단합니다.
      const beforeChattingsLength =
        beforeData.chattings ? beforeData.chattings.length : 0;
      const afterChattingsLength =
        afterData.chattings ? afterData.chattings.length : 0;

      if (afterChattingsLength <= beforeChattingsLength) {
        // 새로운 메시지가 추가되지 않았다면 알림을 보낼 필요가 없습니다.
        return null;
      }

      // 가장 최근에 추가된 메시지 데이터를 가져옵니다.
      const latestMessage = afterData.chattings[afterChattingsLength - 1];

      // chattings 문서에 저장된 onUsers 필드를 가져와 현재 채팅방에 접속 중인 사용자 ID를 확인합니다.
      const onUsers = afterData.onUsers || [];

      // 채팅방에 속한 모든 사용자를 찾기 위해 chattingRooms 문서를 가져옵니다.
      const chatRoomRef = db.collection("chattingRooms").doc(roomId);
      const chatRoomDoc = await chatRoomRef.get();

      if (!chatRoomDoc.exists) {
        console.log(`Chat room document ${roomId} not found.`);
        return null;
      }

      const chatRoomData = chatRoomDoc.data();
      const allRoomUsers = chatRoomData
          .users
          .filter((x) => x.push && x.exist)
          .map((user) => user.userId);

      // 푸시 알림을 받을 대상을 필터링합니다.
      // onUsers에 포함되지 않고, 메시지를 보낸 사람도 아닌 사용자에게 알림을 보냅니다.
      const recipients = allRoomUsers.filter((userId) =>
        !onUsers.includes(userId) && userId !== latestMessage.userId,
      );

      if (recipients.length === 0) {
        console.log("No recipients for notification.");
        return null;
      }

      // 알림 메시지를 보낼 대상 사용자들의 FCM 토큰을 가져옵니다.
      // "users" 컬렉션의 각 문서에 "fcmTokens" 필드가 있다고 가정합니다.
      const tokensPromises = recipients.map((userId) =>
        db.collection("users").doc(userId).get().then((doc) => {
          if (doc.exists) {
            const userData = doc.data();
            // fcmTokens가 배열이라고 가정하고 반환합니다.
            return userData.fcmTokens || [];
          }
          return [];
        }),
      );

      // 모든 토큰을 병렬로 가져옵니다.
      const tokensResult = await Promise.all(tokensPromises);
      const allTokens = tokensResult.flat(); // 중첩된 배열을 평탄화합니다.

      if (allTokens.length === 0) {
        console.log("No FCM tokens to send notifications to.");
        return null;
      }

      // 수정된 payload
      const payload = {
        notification: {
          title: chatRoomData.roomName || "새 채팅 메시지",
          body: `${latestMessage.nickName}: ${latestMessage.msg}`,
        },
        // 'notification' 객체와 같은 레벨로 이동
        data: {
          "roomId": roomId.toString(),
        },
      };

      // 모든 토큰에 메시지를 보냅니다.
      // sendToDevice 대신 sendEachForMulticast를 사용합니다.
      try {
        const response = await admin.messaging()
            .sendEachForMulticast({
              tokens: allTokens,
              notification: payload.notification,
              data: payload.data,
            });
        console.log("Successfully sent message:", response);
        // TODO: "response.responses"를 확인하여 실패한 토큰을 제거하는 로직 추가
      } catch (error) {
        console.log("Error sending message:", error);
      }

      return null;
    });
