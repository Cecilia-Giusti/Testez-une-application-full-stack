describe('admin yoga-app', () => {
  it('Login as admin and navigate to sessions page', () => {
    cy.visit('/login');
    cy.server();
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true,
      },
    });

    cy.intercept('GET', '/api/session', []).as('session');
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type(`${'test!1234'}`);
    cy.get('button[type=submit]').click();
    cy.url().should('include', '/sessions');
  });

  it('Create a new session and verify redirection to create page', () => {
    cy.intercept('GET', '/api/teacher', {
      body: [
        {
          id: 1,
          lastName: 'Smith',
          firstName: 'John',
          createdAt: new Date(),
          updatedAt: new Date(),
        },
        {
          id: 2,
          lastName: 'Dupont',
          firstName: 'Lea',
          createdAt: new Date(),
          updatedAt: new Date(),
        },
      ],
    });
    cy.get('button').contains('Create').click();
    cy.url().should('include', '/sessions/create');

    cy.intercept('POST', '/api/session', {
      body: {
        id: 3,
        name: 'New session',
        description: 'New session description',
        teacher: 1,
        users: [],
      },
    }).as('session');

    cy.intercept('GET', '/api/session', {
      body: [
        {
          id: 1,
          name: 'Session 1',
          description: 'Session yoga pour les débutant',
          date: new Date(),
          createdAt: new Date(),
          updatedAt: new Date(),
          teacher_id: 1,
          users: [1, 2, 3],
        },
        {
          id: 2,
          name: 'Session 2',
          description: 'Session de yoga pour les experts',
          date: new Date(),
          createdAt: new Date(),
          updatedAt: new Date(),
          teacher_id: 1,
          users: [1, 2, 4],
        },
        {
          id: 3,
          name: 'New session',
          description: 'New session description',
          teacher: 1,
          users: [],
        },
      ],
    }).as('sessions');

    cy.get('input[formControlName=name]').type('New session');

    cy.get('input[formControlName=date]').type('2023-12-07');

    cy.get('textarea[formControlName=description]').type(
      'New session description'
    );
    cy.get('mat-select[formControlName=teacher_id]').click();
    cy.get('mat-option').contains('John Smith').click();

    cy.get('button').contains('Save').click();
  });

  it('Verify the presence of the new session in the session list', () => {
    cy.get('.item').should('have.length', 3);
    cy.get('.item').contains('New session').should('exist');
  });

  it('View details of the newly created session', () => {
    cy.intercept('GET', '/api/session/3', {
      body: {
        id: 3,
        name: 'New session',
        description: 'New session description',
        teacher: 1,
        users: [],
      },
    }).as('session');

    cy.intercept('GET', '/api/teacher/1', {
      body: {
        id: 1,
        lastName: 'Smith',
        firstName: 'John',
        createdAt: new Date(),
        updatedAt: new Date(),
      },
    }).as('teacher');

    cy.get('mat-card').last().contains('Detail').last().click();
    cy.url().should('include', '/sessions/detail/3');

    cy.get('div').contains('New session description').should('exist');
  });

  it('Delete a session and verify its removal from the list', () => {
    cy.intercept('DELETE', '/api/session/3', {});
    cy.intercept('GET', '/api/session', {
      body: [
        {
          id: 1,
          name: 'Session 1',
          description: 'Session yoga pour les débutant',
          date: new Date(),
          createdAt: new Date(),
          updatedAt: new Date(),
          teacher_id: 1,
          users: [1, 2, 3],
        },
        {
          id: 2,
          name: 'Session 2',
          description: 'Session yoga pour les experts',
          date: new Date(),
          createdAt: new Date(),
          updatedAt: new Date(),
          teacher_id: 1,
          users: [1, 2, 4],
        },
      ],
    }).as('sessions');

    cy.get('button').contains('Delete').click();

    cy.url().should('contain', '/sessions');
    cy.get('.item').should('have.length', 2);
    cy.get('.item').contains('New session').should('not.exist');
  });
});

describe('user yoga-app', () => {
  it('Registers a new user and navigates to login page', () => {
    cy.visit('/');
    cy.get('[routerlink]').contains('Register').click();

    cy.intercept('POST', '/api/auth/register', {});

    cy.get('input[formControlName=firstName]').type('firstName');
    cy.get('input[formControlName=lastName]').type('lastName');
    cy.get('input[formControlName=email]').type('test@test.com');
    cy.get('input[formControlName=password]').type('test!1234');
    cy.get('button[type=submit]').click();

    cy.url().should('include', '/login');
  });

  it('Logs in and views session list page', () => {
    cy.visit('/login');
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 2,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: false,
      },
    });

    cy.intercept('GET', '/api/session', {
      body: [
        {
          id: 1,
          name: 'Session 1',
          description: 'Session yoga pour les débutant',
          date: new Date(),
          createdAt: new Date(),
          updatedAt: new Date(),
          teacher_id: 1,
          users: [1, 2, 3],
        },
        {
          id: 2,
          name: 'Session 2',
          description: 'Session de yoga pour les experts',
          date: new Date(),
          createdAt: new Date(),
          updatedAt: new Date(),
          teacher_id: 1,
          users: [1, 2, 4],
        },
      ],
    });
    cy.get('input[formControlName=email]').type('testUser@test.fr');
    cy.get('input[formControlName=password]').type(`${'testUser1234!'}`);
    cy.get('button[type=submit]').click();
    cy.url().should('include', '/sessions');
  });

  it('Views details of a specific session', () => {
    cy.intercept('GET', '/api/session/1', {
      body: {
        id: 1,
        name: 'Session 1',
        description: 'Session yoga pour les débutant',
        date: new Date(),
        createdAt: new Date(),
        updatedAt: new Date(),
        teacher_id: 1,
        users: [1, 2, 3],
      },
    }).as('session');

    cy.intercept('GET', '/api/teacher/1', {
      body: {
        id: 1,
        lastName: 'Smith',
        firstName: 'John',
        createdAt: new Date(),
        updatedAt: new Date(),
      },
    }).as('teacher');

    cy.intercept('GET', '/api/session', {
      body: [
        {
          id: 1,
          name: 'Session 1',
          description: 'Session yoga pour les débutant',
          date: new Date(),
          createdAt: new Date(),
          updatedAt: new Date(),
          teacher_id: 1,
          users: [1, 2, 3],
        },
        {
          id: 2,
          name: 'Session 2',
          description: 'Session de yoga pour les experts',
          date: new Date(),
          createdAt: new Date(),
          updatedAt: new Date(),
          teacher_id: 1,
          users: [1, 2, 4],
        },
      ],
    }).as('sessions');

    cy.get('mat-card-actions button').first().click();
    cy.url().should('include', '/sessions/detail/1');
    cy.get('h1').contains('Session 1');
    cy.get('span.ml1').contains('John SMITH');
  });

  it('Returns to session list page from session details', () => {
    cy.get('button').first().click();
    cy.url().should('include', '/sessions');
  });

  it('Navigates to account page and verifies user information', () => {
    cy.intercept('GET', '/api/user/2', {
      id: 2,
      email: 'testUser@test.fr',
      lastName: 'Giusti',
      firstName: 'Cécilia',
      admin: false,
      password: 'testUser1234!',
      createdAt: new Date(),
    });

    cy.get('.link').contains('Account').click();
    cy.url().should('include', '/me');

    cy.get('p').should('exist');
    cy.get('p').should('exist');
  });

  it('Logs out and redirects to home page', () => {
    cy.get('.link').contains('Logout').click();
    cy.url().should('contain', '/');
  });

  it('Redirects to not found page for invalid URLs', () => {
    cy.visit('/notavalidurlforsure');
    cy.url().should('contain', '/404');
  });

  it('Deletes account and verifies logout', () => {
    cy.visit('/login');
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 2,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: false,
      },
    });
    cy.get('input[formControlName=email]').type('testUser@test.fr');
    cy.get('input[formControlName=password]').type('testUser1234!');
    cy.get('button[type=submit]').click();
    cy.url().should('include', '/sessions');

    cy.intercept('GET', '/api/user/2', {
      id: 2,
      email: 'testUser@test.fr',
      lastName: 'Giusti',
      firstName: 'Cécilia',
      admin: false,
      password: 'testUser1234!',
      createdAt: new Date(),
    });
    cy.get('.link').contains('Account').click();
    cy.url().should('include', '/me');

    cy.intercept('DELETE', '/api/user/2', {}).as('deleteAccount');
    cy.get('button').contains('Detail').click();
    cy.wait('@deleteAccount');

    cy.url().should('contain', '/');
  });
});
