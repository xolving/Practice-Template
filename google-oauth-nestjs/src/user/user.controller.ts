import { Controller, Get, Req, UseGuards } from '@nestjs/common';
import { Request } from 'express';
import { RoleGuard } from 'src/guards/role.guard';
import { Role } from './entities/role.enum';
import { Roles } from './entities/roles.decorator';
import { UserService } from './user.service';

@Controller('user')
export class UserController {
  constructor(private readonly userService: UserService) {}

  @Get()
  @Roles(Role.User)
  @UseGuards(RoleGuard)
  async getProfile(@Req() request: Request | any) {
    return this.userService.getProfile(request.user);
  }
}
