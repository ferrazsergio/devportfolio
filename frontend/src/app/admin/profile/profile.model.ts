export interface Profile {
  fullName: string | null;
  username: string | null;
  photoUrl: string | null;
  headline: string | null;
  bio: string | null;
  location: string | null;
  professionalEmail: string | null;
  phone: string | null;
  githubUrl: string | null;
  linkedinUrl: string | null;
  websiteUrl: string | null;
}

export interface UpdateProfileRequest {
  fullName: string;
  username: string;
  photoUrl: string | null;
  headline: string | null;
  bio: string | null;
  location: string | null;
  professionalEmail: string | null;
  phone: string | null;
  githubUrl: string | null;
  linkedinUrl: string | null;
  websiteUrl: string | null;
}
