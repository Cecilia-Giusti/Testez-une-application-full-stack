import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';
import { RegisterComponent } from './register.component';
import { RegisterRequest } from '../../interfaces/registerRequest.interface';
import { of, throwError } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { NgZone } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let mockAuthService: { register: any };

  beforeEach(async () => {
    mockAuthService = { register: jest.fn() };

    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        RouterTestingModule.withRoutes([{ path: 'login', redirectTo: '' }]),
      ],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        FormBuilder,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call register and handle success', () => {
    const spyRouter = jest.spyOn(component['router'], 'navigate');
    const registerRequest: RegisterRequest = {
      email: 'test@example.com',
      firstName: 'test',
      lastName: 'Testing',
      password: 'test!123',
    };

    mockAuthService.register.mockReturnValue(of(registerRequest));

    component.form.setValue({
      email: 'test@example.com',
      firstName: 'test',
      lastName: 'Testing',
      password: 'test!123',
    });

    const ngZone = TestBed.inject(NgZone);
    ngZone.run(() => {
      component.submit();
    });

    expect(spyRouter).toHaveBeenCalledWith(['/login']);
  });

  it('should handle register error', () => {
    mockAuthService.register.mockReturnValue(
      throwError(() => new Error('Login failed'))
    );

    component.form.setValue({
      firstName: 'Test',
      lastName: 'User',
      email: 'test@example.com',
      password: 'password',
    });
    component.submit();

    expect(component.onError).toBe(true);
  });
});
