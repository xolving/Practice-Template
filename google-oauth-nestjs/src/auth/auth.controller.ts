import {
  Controller,
  Delete,
  Get,
  Headers,
  HttpCode,
  Patch,
  Post,
  Req,
  UseGuards,
} from '@nestjs/common';
import { Request } from 'express';
import { RoleGuard } from 'src/guards/role.guard';
import { UserFacade } from 'src/user/facade/user.facade';
import { AuthService } from './auth.service';

@Controller({
  path: 'auth',
  version: '1',
})
export class AuthController {
  constructor(
    private readonly authService: AuthService,
    private readonly userFacade: UserFacade,
  ) {}

  @Get('login/google')
  async signinGoogle(@Req() req: Request) {
    return this.authService.signinGoogle(req.query.code.toString());
  }

  @Patch()
  async refreshToken(@Headers('Refresh-Token') refreshToken: string) {
    return this.authService.refreshToken(refreshToken.substring(7));
  }

  @Post('logout')
  @HttpCode(200)
  async logout(@Headers('Refresh-Token') refreshToken: string) {
    if (refreshToken) {
      this.authService.logout(refreshToken.substring(7));
    }
  }

  @Delete()
  @UseGuards(RoleGuard)
  async withdraw(@Req() request: Request) {
    const user = await this.userFacade.getUser(request);
    this.authService.withdraw(user);
  }
}
