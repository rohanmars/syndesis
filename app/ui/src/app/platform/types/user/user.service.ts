import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import { User } from './user.models';

@Injectable()
export abstract class UserService {
  /**
   * Gets active user as an observable entity
   */
  abstract user: Observable<User>;

  /**
   * Log the user out
   */
  abstract logout(): Observable<any>;
}
