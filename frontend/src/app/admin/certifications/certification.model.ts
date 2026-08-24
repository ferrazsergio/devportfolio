export interface Certification {
  id: string;
  name: string;
  issuingOrganization: string;
  issueDate: string;
  expirationDate: string | null;
  credentialUrl: string | null;
  credentialId: string | null;
}

export interface CertificationRequest {
  name: string;
  issuingOrganization: string;
  issueDate: string;
  expirationDate: string | null;
  credentialUrl: string | null;
  credentialId: string | null;
}
