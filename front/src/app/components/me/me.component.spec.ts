import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { SessionService } from 'src/app/services/session.service';

import { UserService } from 'src/app/services/user.service';
import { of } from 'rxjs';

//Import du composant à tester
import { MeComponent } from './me.component';
import { Component, NgZone } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';
@Component({ template: '' })
class DummyComponent {}

describe('MeComponent', () => {
  // Déclareration des variables pour la fixture et le composant mocké.
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;

  // Déclareration des mocks.
  let mockSessionService: {
    logOut: any;
    sessionInformation?: { id: number };
  };
  let mockUserService: { delete: any; getById?: jest.Mock<any, any, any> };
  let mockMatSnackBar: { open: any };

  beforeEach(async () => {
    // Création des mocks
    mockUserService = {
      getById: jest.fn().mockReturnValue(
        of({
          id: 1,
          firstName: 'Test',
          lastName: 'User',
          email: 'test@example.com',
          admin: true,
        })
      ),
      delete: jest.fn().mockReturnValue(of({})),
    };
    mockSessionService = {
      sessionInformation: { id: 1 },
      logOut: jest.fn(),
    };
    mockMatSnackBar = { open: jest.fn() };

    // Configuration de TestBed pour créer le mock du composant
    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        MatSnackBarModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        RouterTestingModule.withRoutes([
          { path: '', component: DummyComponent },
        ]),
      ],

      //Création des instances de service et configuration des mocks
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: MatSnackBar, useValue: mockMatSnackBar },
        { provide: UserService, useValue: mockUserService },
      ],
    }).compileComponents();

    //Création d'une fixture pour obtenir l'instance du composant
    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize user on ngOnInit', () => {
    expect(mockUserService.getById).toHaveBeenCalledWith('1');

    expect(component.user).toEqual({
      id: 1,
      firstName: 'Test',
      lastName: 'User',
      email: 'test@example.com',
      admin: true,
    });
  });

  it('should call window.history.back when back is called', () => {
    const historySpy = jest.spyOn(window.history, 'back');
    component.back();
    expect(historySpy).toHaveBeenCalled();
  });

  it('should call delete and perform actions on success', () => {
    const spyRouter = jest.spyOn(component['router'], 'navigate');
    const ngZone = TestBed.inject(NgZone);
    ngZone.run(() => {
      component.delete();
    });

    expect(mockUserService.delete).toHaveBeenCalledWith('1');
    expect(mockMatSnackBar.open).toHaveBeenCalledWith(
      'Your account has been deleted !',
      'Close',
      { duration: 3000 }
    );
    expect(mockSessionService.logOut).toHaveBeenCalled();
    expect(spyRouter).toHaveBeenCalledWith(['/']);
  });
});
