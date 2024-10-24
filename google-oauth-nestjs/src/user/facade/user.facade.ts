import { Injectable, Req } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { InjectRepository } from '@nestjs/typeorm';
import { Request } from 'express';
import { Repository } from 'typeorm';
import { User } from '../entities/user.entity';

@Injectable()
export class UserFacade {
  constructor(
    @InjectRepository(User)
    private userRepository: Repository<User>,
    private jwtService: JwtService,
  ) {}

  async getUser(@Req() request: Request) {
    const accessToken = request.headers['Authorization']
      .toString()
      .substring(7);
    const payload = this.jwtService.verify(accessToken);

    return await this.userRepository.findOneBy({ id: payload.sub });
  }
}
