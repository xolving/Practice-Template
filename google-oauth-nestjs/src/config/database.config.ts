import { TypeOrmModuleOptions } from '@nestjs/typeorm';
import { User } from 'src/user/entities/user.entity';
import { config } from './configuration';

export const postgresConfig: TypeOrmModuleOptions = {
  type: 'postgres',
  host: config().database.host,
  port: config().database.port,
  username: config().database.username,
  password: config().database.password,
  database: config().database.database,
  synchronize: true,
  entities: [User],
  autoLoadEntities: true,
};
