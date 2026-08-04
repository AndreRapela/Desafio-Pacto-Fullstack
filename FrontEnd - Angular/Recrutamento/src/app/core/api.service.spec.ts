import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ApiService } from './api.service';

describe('ApiService', () => {
  let service: ApiService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(ApiService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('trims the search term and sends the selected status', () => {
    service.jobs('  angular  ', 'OPEN').subscribe();

    const request = http.expectOne(req =>
      req.url === '/api/jobs' &&
      req.params.get('term') === 'angular' &&
      req.params.get('status') === 'OPEN'
    );

    expect(request.request.method).toBe('GET');
    request.flush([]);
  });

  it('sends status and feedback when evaluating an application', () => {
    service.updateApplication('application-1', 'UNDER_REVIEW', 'Em análise').subscribe();

    const request = http.expectOne('/api/applications/application-1/status');
    expect(request.request.method).toBe('PATCH');
    expect(request.request.body).toEqual({
      status: 'UNDER_REVIEW',
      feedback: 'Em análise'
    });
    request.flush({});
  });
});
