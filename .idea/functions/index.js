/**
 * Firebase Cloud Functions의 메인 파일입니다.
 * 기능별로 분리된 파일들을 불러와서 내보냅니다.
 */
const {setGlobalOptions} = require("firebase-functions");
const admin = require("firebase-admin");

// Admin SDK를 초기화하여 Firestore에 접근 권한을 부여합니다.
// 이 코드는 전체 Cloud Functions에서 한 번만 호출해야 합니다.
admin.initializeApp();

// 전역 옵션을 설정합니다.
setGlobalOptions({maxInstances: 10});

// profile.js 파일에 작성된 모든 함수를 가져와서 내보냅니다.
exports.profile = require("./profile");

// schedule.js 파일에 작성된 모든 함수를 가져와서 내보냅니다.
exports.schedule = require("./schedule");

// chatting-room.js 파일에 작성된 모든 함수를 가져와서 내보냅니다.
exports.chattingRoom = require("./chatting-room");

// chatting-notifications.js 파일에 작성된 모든 함수를 가져와서 내보냅니다.
exports.chattingNotifications = require("./chatting-notifications");
