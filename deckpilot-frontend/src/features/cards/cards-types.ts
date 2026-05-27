export interface CardResponse {
  id: number;
  externalId: number | null;
  name: string;
  cardType: string | null;
  race: string | null;
  attribute: string | null;
  level: number | null;
  atk: number | null;
  defense: number | null;
  description: string | null;
  imageUrl: string | null;
  imageSmallUrl: string | null;
  imageCroppedUrl: string | null;
  source: string;
}
