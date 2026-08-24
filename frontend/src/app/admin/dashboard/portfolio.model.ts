export type PortfolioStatus = 'DRAFT' | 'PUBLISHED';

export interface Portfolio {
  id: string;
  status: PortfolioStatus;
}
