import { OrderStatus } from 'app/entities/enumerations/order-status.model';

export interface IOrder {
  id?: number;
  status?: OrderStatus | null;
  total?: number;
  discount?: number | null;
  fee?: number | null;
  actualTotal?: number | null;
}

export class Order implements IOrder {
  constructor(
    public id?: number,
    public status?: OrderStatus | null,
    public total?: number,
    public discount?: number | null,
    public fee?: number | null,
    public actualTotal?: number | null
  ) {}
}

export function getOrderIdentifier(order: IOrder): number | undefined {
  return order.id;
}
