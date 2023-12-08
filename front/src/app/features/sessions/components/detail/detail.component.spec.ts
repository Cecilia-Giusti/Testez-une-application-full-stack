import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from '../../../../services/session.service';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { DetailComponent } from './detail.component';
import { SessionApiService } from '../../services/session-api.service';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { TeacherService } from 'src/app/services/teacher.service';
import { NgZone } from '@angular/core';

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let service: SessionService;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1,
    },
  };
  let mockSessionApiService: {
    delete: any;
    detail: any;
    participate: any;
    unParticipate: any;
  };
  let mockMatSnackBar: { open: any };
  let mockTeacherService: { detail: any };

  const mockActivatedRoute = {
    snapshot: {
      paramMap: new Map([['id', '1']]),
    },
  };

  beforeEach(async () => {
    mockSessionApiService = {
      delete: jest.fn().mockReturnValue(of({})),
      detail: jest.fn().mockReturnValue(of({ users: [] })),
      participate: jest.fn().mockReturnValue(of(null)),
      unParticipate: jest.fn().mockReturnValue(of(null)),
    };
    mockMatSnackBar = { open: jest.fn() };
    mockTeacherService = { detail: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatSnackBarModule,
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([{ path: 'sessions', redirectTo: '' }]),
        MatIconModule,
        MatCardModule,
      ],
      declarations: [DetailComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: MatSnackBar, useValue: mockMatSnackBar },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: TeacherService, useValue: mockTeacherService },
      ],
    }).compileComponents();
    service = TestBed.inject(SessionService);
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call window.history.back when back is called', () => {
    const historySpy = jest.spyOn(window.history, 'back');
    const ngZone = TestBed.inject(NgZone);
    ngZone.run(() => {
      component.back();
    });

    expect(historySpy).toHaveBeenCalled();
  });

  it('should call delete and perform actions on success', () => {
    const spyRouter = jest.spyOn(component['router'], 'navigate');
    const ngZone = TestBed.inject(NgZone);

    ngZone.run(() => {
      component.sessionId = '1';
      component.delete();
    });

    expect(mockSessionApiService.delete).toHaveBeenCalledWith('1');
    expect(mockMatSnackBar.open).toHaveBeenCalledWith(
      'Session deleted !',
      'Close',
      { duration: 3000 }
    );
    expect(spyRouter).toHaveBeenCalledWith(['sessions']);
  });

  it('should update session and teacher after participate', () => {
    const mockSession = {
      id: 1,
      name: 'Yoga Session',
      description: 'Relaxing yoga session',
      date: new Date('2023-01-01T10:00:00'),
      teacher_id: 2,
      users: [1, 2, 3],
      createdAt: new Date('2023-01-01T09:00:00'),
      updatedAt: new Date('2023-01-01T09:30:00'),
    };

    const mockTeacher = {
      id: 2,
      lastName: 'Doe',
      firstName: 'John',
      createdAt: new Date('2022-12-01T09:00:00'),
      updatedAt: new Date('2022-12-01T10:00:00'),
    };

    mockSessionApiService.detail.mockReturnValue(of(mockSession));
    mockTeacherService.detail.mockReturnValue(of(mockTeacher));

    component.participate();

    expect(component.session).toEqual(mockSession);
    expect(component.teacher).toEqual(mockTeacher);
  });

  it('should call unParticipate and fetch session on success', () => {
    component.sessionId = '1';
    component.userId = '123';

    component.unParticipate();

    expect(mockSessionApiService.unParticipate).toHaveBeenCalledWith(
      '1',
      '123'
    );
  });
});
