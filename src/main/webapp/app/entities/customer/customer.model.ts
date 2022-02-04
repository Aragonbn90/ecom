export interface ICustomer {
  id?: number;
  name?: string;
  address?: string | null;
  age?: number | null;
}

export class Customer implements ICustomer {
  constructor(public id?: number, public name?: string, public address?: string | null, public age?: number | null) {}
}

export function getCustomerIdentifier(customer: ICustomer): number | undefined {
  return customer.id;
}
