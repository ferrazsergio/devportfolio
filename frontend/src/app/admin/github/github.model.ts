export interface GithubStatus {
  connected: boolean;
  githubUsername: string | null;
}

export interface GithubRepo {
  name: string;
  fullName: string;
  description: string | null;
  htmlUrl: string;
  language: string | null;
}

export interface GithubImportResult {
  imported: { id: string; name: string; slug: string; githubUrl: string | null; date: string | null }[];
  skipped: { fullName: string; reason: string }[];
}
