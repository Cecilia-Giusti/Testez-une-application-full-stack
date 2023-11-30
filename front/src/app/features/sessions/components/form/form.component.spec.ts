import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { Router } from '@angular/router';
import { FormComponent } from './form.component';
import { Session } from '../../interfaces/session.interface';
import { of } from 'rxjs';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let sessionServiceMock: any;
  let router: Router;
  let sessionApiServiceMock: any;
  let mockSnackbar: any;

  const mockSession: Session = {
    id: 1,
    name: 'Test Session',
    description: 'Description of Test Session',
    date: new Date('2023-01-01'),
    teacher_id: 100,
    users: [200, 201],
    createdAt: new Date('2023-01-01'),
    updatedAt: new Date('2023-01-02'),
  };

  const expectedSession: Session = {
    id: 1,
    name: 'Test Session',
    description: 'Description of Test Session',
    date: new Date('2023-01-01'),
    teacher_id: 100,
    users: [],
  };

  beforeEach(async () => {
    sessionServiceMock = { sessionInformation: { admin: false } };

    sessionApiServiceMock = {
      detail: jest.fn(),
      create: jest.fn().mockReturnValue(of({ mockSession })),
      update: jest.fn().mockReturnValue(of({ mockSession })),
    };
    mockSnackbar = { open: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([
          { path: 'update', component: FormComponent },
        ]),
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule,
        BrowserAnimationsModule,
      ],
      providers: [
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: SessionApiService, useValue: sessionApiServiceMock },
        { provide: MatSnackBar, useValue: mockSnackbar },
      ],
      declarations: [FormComponent],
    }).compileComponents();

    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate');

    const formBuilder: FormBuilder = TestBed.inject(FormBuilder);

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    component.sessionForm = formBuilder.group({
      name: [expectedSession.name],
      date: [expectedSession],
      teacher_id: [expectedSession.teacher_id],
      description: [expectedSession.description],
    });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect non-admin user', () => {
    fixture.detectChanges();
    expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  it('should load and init form for update if URL contains "update"', () => {
    jest.spyOn(router, 'url', 'get').mockReturnValue('/update');
    sessionApiServiceMock.detail.mockReturnValue(of(mockSession));

    component.ngOnInit();

    expect(component.onUpdate).toBeTruthy();
    expect(component.sessionForm).toBeDefined();

    expect(component.sessionForm?.value).toEqual({
      name: mockSession.name,
      date: mockSession.date.toISOString().split('T')[0],
      teacher_id: mockSession.teacher_id,
      description: mockSession.description,
    });
  });

  it('should init form for create if URL does not contain "update"', () => {
    fixture.detectChanges();

    expect(component.onUpdate).toBeFalsy();
    expect(component.sessionForm).toBeDefined();
  });

  it('should call create method for a new session', () => {
    component.onUpdate = false;

    component.submit();

    expect(sessionApiServiceMock.create).toHaveBeenCalled();

    expect(mockSnackbar.open).toHaveBeenCalledWith(
      'Session created !',
      'Close',
      { duration: 3000 }
    );
  });

  it('should call update method for an existing session', () => {
    component.onUpdate = true;

    component['id'] = '1';

    component.submit();

    expect(sessionApiServiceMock.update).toHaveBeenCalled();
  });
});
