import { ComponentFixture, TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

//Import du composant à tester
import { NotFoundComponent } from './not-found.component';

describe('NotFoundComponent', () => {
  // Déclareration des variables pour la fixture et le composant mocké.
  let component: NotFoundComponent;
  let fixture: ComponentFixture<NotFoundComponent>;

  beforeEach(async () => {
    // Configuration de TestBed pour créer le mock du composant
    await TestBed.configureTestingModule({
      declarations: [NotFoundComponent],
    }).compileComponents();

    //Création d'une fixture pour obtenir l'instance du composant
    fixture = TestBed.createComponent(NotFoundComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
