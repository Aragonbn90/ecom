export interface IProduct {
  id?: number;
  name?: string;
  description?: string | null;
  price?: number | null;
  discountPercent?: number | null;
  newPrice?: number | null;
  instructions?: string | null;
  netQuantity?: number | null;
  netQuantityUnit?: string | null;
  imageUrls?: string | null;
  ingredients?: string | null;
}

export class Product implements IProduct {
  constructor(
    public id?: number,
    public name?: string,
    public description?: string | null,
    public price?: number | null,
    public discountPercent?: number | null,
    public newPrice?: number | null,
    public instructions?: string | null,
    public netQuantity?: number | null,
    public netQuantityUnit?: string | null,
    public imageUrls?: string | null,
    public ingredients?: string | null
  ) {}
}

export function getProductIdentifier(product: IProduct): number | undefined {
  return product.id;
}
