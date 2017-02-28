package net.sf.robocode.battle;

import robocode.control.snapshot.IScoreSnapshot;

/**
 * @author Thales B.V. / Colin Heppener (Naval Robocode contributor)
 */
public class NavalBattleRankingTableModel extends BattleRankingTableModel {
    public int getColumnCount() {
        return 16;
    }

    @Override
    public String getColumnName(int col) {
        switch (col) {
            case 0:
                return "Rank";

            case 1:
                return "Ship Name";

            case 2:
                return "    Total Score    ";

            case 3:
                return "Survival";

            case 4:
                return "Surv Bonus";

            case 5:
                return "Bullet Dmg";

            case 6:
                return "Bullet Bonus";

            case 7:
                return "Missile Dmg";
            case 8:
                return "Missile Bonus";

            case 9:
                return "Ram Dmg * 2";

            case 10:
                return "Ram Bonus";

            case 11:
                return "Mine Damage";

            case 12:
                return "Mine Bonus";

            case 13:
                return " 1sts ";

            case 14:
                return " 2nds ";

            case 15:
                return " 3rds ";

            default:
                return "";
        }
    }

    public Object getValueAt(int row, int col) {
        final IScoreSnapshot statistics = scoreSnapshotList[row];
        switch (col) {
            case 0:
                return getPlacementString(row + 1);

            case 1:
                return statistics.getName();

            case 2: {
                final double current = statistics.getCurrentScore();
                final double total = statistics.getTotalScore();

                return (int) (current + 0.5) + " / " + (int) (total + current + 0.5) + "  ("
                        + (int) (current / currentSum * 100) + " / " + (int) ((total + current) / (totalSum + currentSum) * 100)
                        + "%)";
            }

            case 3: {
                final double current = statistics.getCurrentSurvivalScore();
                final double total = statistics.getTotalSurvivalScore();

                return (int) (current + 0.5) + " / " + (int) (total + current + 0.5);
            }

            case 4:
                return (int) (statistics.getTotalLastSurvivorBonus() + 0.5);

            case 5: {
                final double current = statistics.getCurrentBulletDamageScore();
                final double total = statistics.getTotalBulletDamageScore();

                return (int) (current + 0.5) + " / " + (int) (total + current + 0.5);
            }

            case 6: {
                final double current = statistics.getCurrentBulletKillBonus();
                final double total = statistics.getTotalBulletKillBonus();

                return (int) (current + 0.5) + " / " + (int) (total + current + 0.5);
            }

            case 7: {
                final double current = statistics.getCurrentMissileDamageScore();
                final double total = statistics.getTotalMissileDamageScore();

                return (int) (current + 0.5) + " / " + (int) (total + current + 0.5);
            }

            case 8: {
                final double current = statistics.getCurrentMissileKillBonus();
                final double total = statistics.getTotalMissileKillBonus();

                return (int) (current + 0.5) + " / " + (int) (total + current + 0.5);
            }

            case 9: {
                final double current = statistics.getCurrentRammingDamageScore();
                final double total = statistics.getTotalRammingDamageScore();

                return (int) (current + 0.5) + " / " + (int) (total + current + 0.5);
            }

            case 10: {
                final double current = statistics.getCurrentRammingKillBonus();
                final double total = statistics.getTotalRammingKillBonus();

                return (int) (current + 0.5) + " / " + (int) (total + current + 0.5);
            }

            case 11: {
                final double current = statistics.getCurrentMineDamageScore();
                final double total = statistics.getTotalMineDamageScore();

                return (int) (current + 0.5) + " / " + (int) (total + current + 0.5);
            }

            case 12: {
                final double current = statistics.getCurrentMineKillBonus();
                final double total = statistics.getTotalMineKillBonus();

                return (int) (current + 0.5) + " / " + (int) (total + current + 0.5);
            }

            case 13:
                return "" + statistics.getTotalFirsts();

            case 14:
                return "" + statistics.getTotalSeconds();

            case 15:
                return "" + statistics.getTotalThirds();

            default:
                return "";
        }
    }

}
