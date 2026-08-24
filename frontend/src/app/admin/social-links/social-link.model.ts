export interface SocialLink {
  id: string;
  platform: string;
  url: string;
  order: number;
}

export interface SocialLinkRequest {
  platform: string;
  url: string;
  order: number;
}
