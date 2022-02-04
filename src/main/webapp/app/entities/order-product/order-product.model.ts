import { IOrder } from 'app/entities/order/order.model';
import { IProduct } from 'app/entities/product/product.model';

export interface IOrderProduct {
  id?: number;
  price?: number | null;
  quantity?: number;
  total?: number | null;
  order?: IOrder;
  product?: IProduct;
}

export class OrderProduct implements IOrderProduct {
  constructor(
    public id?: number,
    public price?: number | null,
    public quantity?: number,
    public total?: number | null,
    public order?: IOrder,
    public product?: IProduct
  ) {}
}

export function getOrderProductIdentifier(orderProduct: IOrderProduct): number | undefined {
  return orderProduct.id;
}
