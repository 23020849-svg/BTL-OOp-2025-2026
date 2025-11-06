package arkanoid.entities.powerups;

import java.util.Random;

public class PowerUpFactory {

    public enum PowerUpType{
        EXPAND_PADDLE,
        FAST_BALL,
        MULTI_BALL,
        LASER,
    }

    private static final Random random = new Random();

    /** tao poweup ngau nhien */
    public static PowerUp createRandomPowerUp(int x, int y) {
        PowerUpType[] types = PowerUpType.values();
        PowerUpType randomType = types[random.nextInt(types.length)];
        return createPowerUp(randomType, x, y);
    }

    public static PowerUp createPowerUp(PowerUpType type, int x, int y) {
        switch (type) {
            case EXPAND_PADDLE:
                return new ExpandPaddlePowerUp(x, y);
            case FAST_BALL:
                return new FastBallPowerUp(x, y);
            case MULTI_BALL:
                return new MultiBallPowerUp(x, y);
            case LASER:
                return new LaserPowerUp(x, y);
            default:
             return new ExpandPaddlePowerUp(x, y);

        }
    }

    /** tao powerup theo ty le phan tram */

    public static PowerUp createWeightedRandomPowerUp(int x, int y) {
            double rand = random.nextDouble();
            if(rand < 0.30) {
                return new ExpandPaddlePowerUp(x, y);
            }

            else if (rand < 0.55) {
                return new FastBallPowerUp(x, y);
            }

            else if (rand < 0.75) {
                return new MultiBallPowerUp(x, y);
            }

            else {
                return new LaserPowerUp(x, y);
            }
    }
    
}
