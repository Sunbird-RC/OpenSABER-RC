-- CreateEnum
CREATE TYPE "BlockchainStatusEnum" AS ENUM ('PENDING', 'ANCHORED', 'FAILED');

-- AlterTable
ALTER TABLE "VerifiableCredentials" ADD COLUMN     "blockchainStatus" "BlockchainStatusEnum" NOT NULL DEFAULT 'PENDING';
