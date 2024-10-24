import {
  BadRequestException,
  CanActivate,
  ExecutionContext,
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';
import { Reflector } from '@nestjs/core';
import { JsonWebTokenError, JwtService, TokenExpiredError } from '@nestjs/jwt';
import { InjectRepository } from '@nestjs/typeorm';
import { Request } from 'express';
import { Role } from 'src/user/entities/role.enum';
import { ROLES_KEY } from 'src/user/entities/roles.decorator';
import { User } from 'src/user/entities/user.entity';
import { Repository } from 'typeorm';

@Injectable()
export class RoleGuard implements CanActivate {
  constructor(
    @InjectRepository(User)
    private userRepository: Repository<User>,
    private jwtService: JwtService,
    private reflector: Reflector,
  ) {}

  async canActivate(context: ExecutionContext): Promise<boolean> {
    const request: Request = context.switchToHttp().getRequest();
    const token = this.validateToken(request.headers.authorization);

    const requiredRoles = this.reflector.getAllAndOverride<Role[]>(ROLES_KEY, [
      context.getHandler(),
      context.getClass(),
    ]);

    if (!token) {
      throw new UnauthorizedException('토큰의 헤더가 일치하지 않습니다.');
    }

    try {
      const payload = this.jwtService.verify(token);
      const user: User = await this.userRepository.findOneBy({
        id: payload.sub,
      });
      request['user'] = user;

      if (!requiredRoles) {
        return true;
      }

      return requiredRoles.some((role) => user.roles.includes(role));
    } catch (error) {
      if (error instanceof TokenExpiredError) {
        throw new UnauthorizedException('Access Token is expired');
      } else if (error instanceof SyntaxError) {
        throw new BadRequestException('Invalid JSON object');
      } else if (error instanceof JsonWebTokenError) {
        throw new BadRequestException(error.message);
      } else {
        throw new UnauthorizedException('Unauthorized for unknown error');
      }
    }
  }

  validateToken(includedToken: string | string[]) {
    const [header, token] = includedToken.toString().split(' ');
    return header === 'Bearer' ? token : null;
  }
}
