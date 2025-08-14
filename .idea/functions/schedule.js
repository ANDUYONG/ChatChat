/**
 * 예약된 작업과 관련된 Firebase Cloud Functions를 모아둔 파일입니다.
 */
const {onSchedule} = require("firebase-functions/v2/scheduler");
const logger = require("firebase-functions/logger");
const admin = require("firebase-admin");

/**
 * 매일 오후 7시(19:00)에 실행되는 예약 함수입니다. (한국 시간)
 * Firebase Storage에만 존재하고 Firestore에 없는 파일을 삭제합니다.
 */
exports.cleanUpOrphanedStorageImages = onSchedule(
    {
      schedule: "every day 13:19",
      timeZone: "Asia/Seoul", // 한국 시간(KST)으로 설정
    },
    async (event) => {
      logger.info(
          "Starting scheduled orphaned image cleanup task in Storage.",
      );

      const db = admin.firestore();
      const bucket = admin.storage().bucket();
      const prefix = "chatchat/profiles";

      try {
        const usersSnapshot = await db.collection("users").get();
        const usedFilePaths = new Set();

        usersSnapshot.forEach((doc) => {
          const userData = doc.data();

          if (userData.profileUrl && userData.profileUrl !== "") {
            if (userData.profileUrl) usedFilePaths.add(userData.profileUrl);
          }

          if (Array.isArray(userData.backgroundUrls)) {
            userData.backgroundUrls.forEach((url) => {
              if (userData.profileUrl) usedFilePaths.add(url);
            });
          }
        });

        const [files] = await bucket.getFiles({prefix: prefix});
        logger.info(`Found ${files.length} files in Storage.`);

        for (const file of files) {
          const storageFilePath = file.name;
          if (!usedFilePaths.has(storageFilePath)) {
            logger.info(`File '${storageFilePath}' not found`);
            await file.delete();
          } else {
            logger.info(
                `File '${storageFilePath}' found in Firestore. Keeping.`,
            );
          }
        }
        logger.info(
            "Orphaned image cleanup task finished successfully.",
        );
      } catch (error) {
        logger.error("An error occurred during the cleanup task:", error);
      }
    });

