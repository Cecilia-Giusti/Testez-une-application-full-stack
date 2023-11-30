import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';

import { AppComponent } from './app.component';
import { of } from 'rxjs';
import { AuthService } from './features/auth/services/auth.service';
import { SessionService } from './services/session.service';
import { Router } from '@angular/router';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let sessionServiceMock: { $isLogged: any; logOut: any };
  let routerMock: { navigate: any };

  beforeEach(async () => {
    sessionServiceMock = { $isLogged: jest.fn(), logOut: jest.fn() };
    routerMock = { navigate: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientModule, MatToolbarModule],
      declarations: [AppComponent],
      providers: [
        { provide: AuthService, useValue: {} },
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: Router, useValue: routerMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should return logged in status', () => {
    sessionServiceMock.$isLogged.mockReturnValue(of(true));

    const result = component.$isLogged();

    result.subscribe((isLogged) => {
      expect(isLogged).toBe(true);
    });
  });

  it('should call logOut and navigate to home on logout', () => {
    component.logout();

    expect(sessionServiceMock.logOut).toHaveBeenCalled();

    expect(routerMock.navigate).toHaveBeenCalledWith(['']);
  });
});
