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
