package arkanoid.entities.powerups;

import java.util.Random;

/**
 * PowerUpFactory - Factory Method Pattern
 * Tạo power-ups theo các strategy khác nhau
 */
public class PowerUpFactory {

    private static final Random random = new Random();

    /**
     * Enum định nghĩa các loại power-up
     */
    public enum PowerUpType {
        EXPAND_PADDLE,
        FAST_BALL,
        MULTI_BALL,
        LASER
    }

    /**
     * Factory Method - Tạo power-up theo type
     */
    public static PowerUp createPowerUp(PowerUpType type, int x, int y) {
        return switch (type) {
            case EXPAND_PADDLE -> createExpandPaddle(x, y);
            case FAST_BALL -> createFastBall(x, y);
            case MULTI_BALL -> createMultiBall(x, y);
            case LASER -> createLaser(x, y);
            default -> createExpandPaddle(x, y);
        };
    }

    /**
     * Tạo power-up ngẫu nhiên (đều đặn)
     */
    public static PowerUp createRandomPowerUp(int x, int y) {
        PowerUpType[] types = PowerUpType.values();
        PowerUpType randomType = types[random.nextInt(types.length)];
        return createPowerUp(randomType, x, y);
    }

    /**
     * Tạo power-up theo tỷ lệ phần trăm
     * - Expand Paddle: 30%
     * - Fast Ball: 25%
     * - Multi Ball: 20%
     * - Laser: 25%
     */
    public static PowerUp createWeightedRandomPowerUp(int x, int y) {
        double rand = random.nextDouble();
        double expandThreshold = PowerUpConfig.EXPAND_PADDLE_DROP_WEIGHT;
        double fastThreshold = expandThreshold + PowerUpConfig.FAST_BALL_DROP_WEIGHT;
        double multiThreshold = fastThreshold + PowerUpConfig.MULTI_BALL_DROP_WEIGHT;

         if (rand < expandThreshold) {
            return createExpandPaddle(x, y);
        } else if (rand < fastThreshold) {
            return createFastBall(x, y);
        } else if (rand < multiThreshold) {
            return createMultiBall(x, y);
        } else {
            return createLaser(x, y);
        }
    }


    /**
     * Tạo Expand Paddle Power-Up
     */
    private static PowerUp createExpandPaddle(int x, int y) {
        return new ExpandPaddlePowerUp(x, y);
    }

    /**
     * Tạo Fast Ball Power-Up
     */
    private static PowerUp createFastBall(int x, int y) {
        return new FastBallPowerUp(x, y);
    }

    /**
     * Tạo Multi Ball Power-Up
     */
    private static PowerUp createMultiBall(int x, int y) {
        return new MultiBallPowerUp(x, y);
    }

    /**
     * Tạo Laser Power-Up
     */
    private static PowerUp createLaser(int x, int y) {
        return new LaserPowerUp(x, y);
    }

    // ========== UTILITY METHODS ==========

    /**
     * Tạo power-up theo tên (string)
     * Hữu ích khi load từ file save
     */
    public static PowerUp createPowerUpByName(String name, int x, int y) {
        if (name == null) return null;
        
        switch (name.toLowerCase()) {
            case "expand", "expand_paddle" -> {
                return createExpandPaddle(x, y);
            }
            case "fast", "fast_ball" -> {
                return createFastBall(x, y);
            }
            case "multi", "multi_ball" -> {
                return createMultiBall(x, y);
            }
            case "laser" -> {
                return createLaser(x, y);
            }
            default -> {
                System.err.println("Unknown power-up type: " + name);
                return null;
            }
        }
    }

    /**
     * Lấy tên của power-up type
     */
    public static String getTypeName(PowerUpType type) {
        return switch (type) {
            case EXPAND_PADDLE -> "expand";
            case FAST_BALL -> "fast";
            case MULTI_BALL -> "multi";
            case LASER -> "laser";
            default -> "unknown";
        };
    }

    /**
     * Xác định type từ instance của power-up
     */
    public static String getTypeName(PowerUp powerUp) {
        if (powerUp instanceof ExpandPaddlePowerUp) return "expand";
        if (powerUp instanceof FastBallPowerUp) return "fast";
        if (powerUp instanceof MultiBallPowerUp) return "multi";
        if (powerUp instanceof LaserPowerUp) return "laser";
        return "unknown";
    }
}