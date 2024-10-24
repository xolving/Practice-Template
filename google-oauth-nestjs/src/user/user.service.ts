import { Injectable } from '@nestjs/common';
import { User } from './entities/user.entity';

@Injectable()
export class UserService {
  getProfile(user: User) {
    return user.id;
  }
}
