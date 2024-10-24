import { config } from './configuration';

export const redisConfig = {
  host: config().redis.host,
  port: config().redis.port,
};
