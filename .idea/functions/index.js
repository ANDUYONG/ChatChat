/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */
const {onDocumentCreated} = require("firebase-functions/v2/firestore");
// v2 Firestore onCreate 트리거 임포트

const {setGlobalOptions} = require("firebase-functions");
// const {onRequest} = require("firebase-functions/https");
const logger = require("firebase-functions/logger");
const admin = require("firebase-admin"); // Firebase Admin SDK 임포트 및 초기화

admin.initializeApp(); // Firebase Admin SDK 초기화

// For cost control, you can set the maximum number of containers that can be
// running at the same time. This helps mitigate the impact of unexpected
// traffic spikes by instead downgrading performance. This limit is a
// per-function limit. You can override the limit for each function using the
// `maxInstances` option in the function's options, e.g.
// `onRequest({ maxInstances: 5 }, (req, res) => { ... })`.
// NOTE: setGlobalOptions does not apply to functions using the v1 API. V1
// functions should each use functions.runWith({ maxInstances: 10 }) instead.
// In the v1 API, each function can only serve one request per container, so
// this will be the maximum concurrent request count.
setGlobalOptions({maxInstances: 10});

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
// --- 여기에 Cloud Function 코드 추가 ---

/**
 * 'users' 컬렉션에 새로운 문서가 생성될 때마다 트리거되는 함수입니다.
 * 생성된 사용자의 기본 정보를 'profiles' 컬렉션에 복사하여 프로필 문서를 생성합니다.
 */
exports.createProfileForNewUser = onDocumentCreated(
    "users/{userId}", async (event) => {
      // 1. 트리거된 문서의 데이터와 ID 가져오기
      const snapshot = event.data;
      if (!snapshot) {
        logger.error("No data associated with the event.");
        return; // 데이터가 없으면 종료
      }

      const newUser = snapshot.data(); // 새로 생성된 user 문서의 데이터
      const userId = event.params.userId; // 새로 생성된 user 문서의 ID

      logger.info(`New user created with ID: ${userId}`, newUser);

      // 2. profiles 컬렉션에 저장할 데이터 구성
      // users 문서의 데이터를 그대로 사용하거나, 필요한 필드만 추출/가공합니다.
      const profileData = {
        userId: userId,
        email: newUser.email || null, // 이메일 필드가 없을 경우를 대비 (null 처리)
        name: newUser.name || "새 사용자", // 이름 필드가 없을 경우 기본값
        nickName: newUser.nickName || "새 사용자",
        statusMsg: "",
        profileUrl: "",
        backgroundUrls: [],
        // Firestore의 서버 타임스탬프를 사용하여 정확한 생성 시간 기록
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
        // 필요에 따라 여기에 다른 기본 프로필 필드를 추가할 수 있습니다.
        // 예: profileImageUrl: 'https://example.com/default_profile.png',
        //     bio: '안녕하세요!',
      };

      // 3. 'profiles' 컬렉션에 문서 쓰기
      try {
        // 'profiles' 컬렉션에 userId를 문서 ID로 사용하여 데이터 추가
        // .set()을 사용하면 해당 ID의 문서가 없으면 생성하고, 있으면 덮어씁니다.
        // (일반적으로 createUserProfile에서는 .set()이 안전합니다)
        await admin.firestore()
            .collection("profiles")
            .doc(userId).set(profileData);
        logger.info(`Profile successfully created for user: ${userId}`);
        return null; // 함수 성공적으로 종료
      } catch (error) {
        logger.error(`Error creating profile for user ${userId}:`, error);
        // 오류 발생 시 Firestore 로그에 기록됩니다.
        // 클라이언트에게 직접적인 영향을 주지는 않지만, 문제 추적에 중요합니다.
        return null; // 에러가 발생했더라도 함수는 종료 (Cloud Functions가 재시도할 수 있음)
      }
    });


/**
 * 'users' 컬렉션의 문서가 업데이트될 때마다 트리거되는 함수입니다.
 * profileUrl 또는 backgroundUrls가 변경되면 'profiles' 컬렉션에 해당 필드를 업데이트합니다.
 */
const {onDocumentUpdated} = require("firebase-functions/v2/firestore");

exports.updateProfileFromUser = onDocumentUpdated(
    "users/{userId}", async (event) => {
      const change = event.data;
      if (!change) {
        logger.error("No data associated with the event.");
        return;
      }

      const userId = event.params.userId;
      const beforeData = change.before.data(); // 업데이트 전 데이터
      const afterData = change.after.data(); // 업데이트 후 데이터

      logger.info(`User document updated for ID: ${userId}`);

      // 1. 업데이트할 필드들을 담을 객체 초기화
      const updateData = {};

      // 2. profileUrl 필드의 변경 여부 확인
      if (beforeData.profileUrl !== afterData.profileUrl) {
        logger.info(`profileUrl changed for user ${userId}.`);
        updateData.profileUrl = afterData.profileUrl;
      }

      // 3. backgroundUrls 필드의 변경 여부 확인
      // 배열은 직접 비교가 어려우므로 JSON.stringify를 통해 비교합니다.
      if (JSON.stringify(beforeData.backgroundUrls) !==
          JSON.stringify(afterData.backgroundUrls)) {
        logger.info(`backgroundUrls changed for user ${userId}.`);
        updateData.backgroundUrls = afterData.backgroundUrls;
      }

      // 4. 변경 사항이 있을 경우에만 Firestore 업데이트
      if (Object.keys(updateData).length > 0) {
        try {
          await admin.firestore()
              .collection("profiles")
              .doc(userId)
              .update(updateData);
          logger.info(`Profiles collection updated for user ${userId}.`,
              updateData);
        } catch (error) {
          logger.error(`Error updating profiles collection for user ${userId}:`,
              error);
        }
      } else {
        logger.info(`${userId}. No update needed.`);
      }

      return null;
    });

/**
 * Import function triggers for scheduled tasks.
 */
const {onSchedule} = require("firebase-functions/v2/scheduler");

/**
 * 매일 오후 7시(19:00)에 실행되는 예약 함수입니다. (한국 시간)
 * Firebase Storage에만 존재하고 Firestore에 없는 파일을 삭제합니다.
 */
exports.cleanUpOrphanedStorageImages = onSchedule({
  schedule: "every day 18:23",
  timeZone: "Asia/Seoul", // 한국 시간(KST)으로 설정
}, async (event) => {
  logger.info("Starting scheduled orphaned image cleanup task in Storage.");

  const db = admin.firestore();
  const bucket = admin.storage().bucket();
  const prefix = "chatchat/profiles"; // 프로필 이미지 저장 경로

  try {
    // 1. Firestore의 모든 유저 문서에서 사용 중인 모든 파일 경로를 Set에 저장
    const usersSnapshot = await db.collection("users").get();
    const usedFilePaths = new Set(); // 중복을 피하기 위해 Set 사용

    usersSnapshot.forEach((doc) => {
      const userData = doc.data();

      // profileUrl에서 파일 경로 추출 후 Set에 추가
      if (userData.profileUrl && userData.profileUrl !== "") {
        const filePath = getFilePathFromUrl(userData.profileUrl);
        if (filePath) usedFilePaths.add(filePath);
      }

      // backgroundUrls 배열에서 파일 경로 추출 후 Set에 추가
      if (Array.isArray(userData.backgroundUrls)) {
        userData.backgroundUrls.forEach((url) => {
          const filePath = getFilePathFromUrl(url);
          if (filePath) usedFilePaths.add(filePath);
        });
      }
    });

    // 2. Storage의 특정 경로에 있는 모든 파일 목록 가져오기
    const [files] = await bucket.getFiles({prefix: prefix});
    logger.info(`Found ${files.length} files in Storage.`);

    // 3. Storage의 각 파일 경로가 Firestore의 Set에 있는지 확인
    for (const file of files) {
      const storageFilePath = file.name;

      // Firestore의 파일 경로 목록에 Storage 파일 경로가 포함되어 있는지 확인
      if (!usedFilePaths.has(storageFilePath)) {
        logger.info(`File '${storageFilePath}' not found`);
        await file.delete();
      } else {
        logger.info(`File '${storageFilePath}' found in Firestore. Keeping.`);
      }
    }
    logger.info("Orphaned image cleanup task finished successfully.");
  } catch (error) {
    logger.error("An error occurred during the cleanup task:", error);
  }
});

/**
 * Firebase Storage 다운로드 URL에서 파일 경로를 추출하는 헬퍼 함수
 * @param {string} url Firebase Storage 다운로드 URL
 * @return {string} 파일 경로 (예: 'chatchat/profiles/userId/image.jpg')
 */
function getFilePathFromUrl(url) {
  // URL에서 'o/' 이후의 경로를 추출
  const pathStartIndex = url.indexOf("/o/") + 3;
  if (pathStartIndex < 3) return "";

  const pathEndIndex = url.indexOf("?alt=media");
  if (pathEndIndex === -1) return "";

  const filePath = url.substring(pathStartIndex, pathEndIndex);

  // URL 인코딩된 문자열을 디코딩
  return decodeURIComponent(filePath);
}
