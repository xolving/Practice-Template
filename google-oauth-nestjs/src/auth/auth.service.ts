import {
  Injectable,
  NotFoundException,
  UnauthorizedException,
} from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { InjectRepository } from '@nestjs/typeorm';
import axios from 'axios';
import { Role } from 'src/user/entities/role.enum';
import { User } from 'src/user/entities/user.entity';
import { UserFacade } from 'src/user/facade/user.facade';
import { Repository } from 'typeorm';
import { config } from '../config/configuration';
import { RedisRepository } from './redis.repository';

@Injectable()
export class AuthService {
  constructor(
    @InjectRepository(User)
    private readonly userRepository: Repository<User>,
    private readonly jwtService: JwtService,
    private readonly redisRepository: RedisRepository,
    private readonly userFacade: UserFacade,
  ) {}

  async signinGoogle(code: string) {
    const tokens = await this.getGoogleTokens(code);
    const userInfo = await this.getGoogleUserInfo(tokens.access_token);

    this.existOrSaveUser(userInfo.email);

    const user = await this.userRepository.findOneBy({
      email: userInfo.email,
    });

    const tokenSet = await this.generateTokenSet(user.id);
    this.saveRefreshToken(tokenSet.refresh_token, user.id);

    return tokenSet;
  }

  async refreshToken(token: string) {
    const savedTokenUserId = await this.redisRepository.get(token);
    if (!savedTokenUserId) {
      throw new NotFoundException('유효하는 리프레시 토큰을 찾을 수 없습니다.');
    }

    const user = await this.userRepository.findOneBy({ id: savedTokenUserId });
    if (!user) {
      throw new NotFoundException('해당 유저를 찾을 수 없습니다.');
    }

    const newAccessToken = await this.generateToken(user.id, 'AccessToken');

    return {
      access_token: newAccessToken,
    };
  }

  async logout(refreshToken?: string) {
    const savedTokenUserId = await this.redisRepository.get(refreshToken);
    const user = await this.userRepository.findOneBy({ id: savedTokenUserId });
    if (!user) {
      throw new NotFoundException('해당 유저를 찾을 수 없습니다.');
    }

    this.redisRepository.delete(refreshToken);
  }

  async withdraw(user: User) {
    this.userRepository.delete({ id: user.id });
  }

  private async saveRefreshToken(token: string, id: string) {
    this.redisRepository.set(token, id, config().jwt.refreshExpires);
  }

  private async existOrSaveUser(email: string) {
    if (!(await this.userRepository.existsBy({ email: email }))) {
      await this.userRepository.save({
        email: email,
        roles: [Role.Admin],
      });
    }
  }

  private async generateTokenSet(id: string) {
    return {
      access_token: await this.generateToken(id, 'AccessToken'),
      refresh_token: await this.generateToken(id, 'RefreshToken'),
    };
  }

  private async generateToken(
    id: string,
    type: 'AccessToken' | 'RefreshToken',
  ) {
    const token = this.jwtService.sign(
      { sub: id },
      {
        secret:
          type == 'AccessToken'
            ? config().jwt.accessSecret
            : config().jwt.refreshSecret,
        expiresIn:
          type == 'AccessToken'
            ? config().jwt.accessExpires
            : config().jwt.refreshExpires,
      },
    );

    return token;
  }

  private async getGoogleTokens(code: string) {
    const url = 'https://oauth2.googleapis.com/token';
    const bodyData = {
      code: code,
      client_id: config().google.clientId,
      client_secret: config().google.clientSecret,
      redirect_uri: config().google.callbackUrl,
      grant_type: 'authorization_code',
    };

    try {
      const { data } = await axios.post(url, new URLSearchParams(bodyData), {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
      });
      return data;
    } catch (error) {
      throw new UnauthorizedException('Google 코드가 유효하지 않습니다.');
    }
  }

  private async getGoogleUserInfo(accessToken: string) {
    const url = 'https://www.googleapis.com/oauth2/v2/userinfo';

    try {
      const { data } = await axios.get(url, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      return data;
    } catch (error) {
      throw new UnauthorizedException(
        'Google 엑세스 토큰이 유효하지 않습니다.',
      );
    }
  }
}
