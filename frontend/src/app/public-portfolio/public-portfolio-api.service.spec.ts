import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { PublicPortfolioApiService } from './public-portfolio-api.service';
import { PublicPortfolio } from './public-portfolio.model';

describe('PublicPortfolioApiService', () => {
  let service: PublicPortfolioApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(PublicPortfolioApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('requests the public portfolio by username', () => {
    const mockResponse: PublicPortfolio = {
      profile: {
        fullName: 'Ana Souza',
        headline: null,
        bio: null,
        location: null,
        professionalEmail: null,
        phone: null,
        githubUrl: null,
        linkedinUrl: null,
        websiteUrl: null,
        photoUrl: null,
        socialLinks: [],
      },
      experiences: [],
      projects: [],
      skills: [],
      educations: [],
      certifications: [],
    };

    service.getByUsername('ana-souza').subscribe((result) => {
      expect(result).toEqual(mockResponse);
    });

    const req = httpMock.expectOne('/api/v1/public/ana-souza');
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });
});
