import { Injectable } from '@nestjs/common';
import Redis from 'ioredis';
import { redisConfig } from 'src/config/redis.config';

@Injectable()
export class RedisRepository {
  private readonly redisClient: Redis;

  constructor() {
    this.redisClient = new Redis(redisConfig);
  }

  async get(key: string) {
    return this.redisClient.get(key);
  }

  async set(key: string, value: string, ttl?: number) {
    return ttl
      ? this.redisClient.set(key, value, 'EX', ttl)
      : this.redisClient.set(key, value);
  }

  async delete(key: string) {
    return this.redisClient.del(key);
  }
}
