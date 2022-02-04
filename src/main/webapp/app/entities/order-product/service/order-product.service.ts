import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IOrderProduct, getOrderProductIdentifier } from '../order-product.model';

export type EntityResponseType = HttpResponse<IOrderProduct>;
export type EntityArrayResponseType = HttpResponse<IOrderProduct[]>;

@Injectable({ providedIn: 'root' })
export class OrderProductService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/order-products');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(orderProduct: IOrderProduct): Observable<EntityResponseType> {
    return this.http.post<IOrderProduct>(this.resourceUrl, orderProduct, { observe: 'response' });
  }

  update(orderProduct: IOrderProduct): Observable<EntityResponseType> {
    return this.http.put<IOrderProduct>(`${this.resourceUrl}/${getOrderProductIdentifier(orderProduct) as number}`, orderProduct, {
      observe: 'response',
    });
  }

  partialUpdate(orderProduct: IOrderProduct): Observable<EntityResponseType> {
    return this.http.patch<IOrderProduct>(`${this.resourceUrl}/${getOrderProductIdentifier(orderProduct) as number}`, orderProduct, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IOrderProduct>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IOrderProduct[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addOrderProductToCollectionIfMissing(
    orderProductCollection: IOrderProduct[],
    ...orderProductsToCheck: (IOrderProduct | null | undefined)[]
  ): IOrderProduct[] {
    const orderProducts: IOrderProduct[] = orderProductsToCheck.filter(isPresent);
    if (orderProducts.length > 0) {
      const orderProductCollectionIdentifiers = orderProductCollection.map(
        orderProductItem => getOrderProductIdentifier(orderProductItem)!
      );
      const orderProductsToAdd = orderProducts.filter(orderProductItem => {
        const orderProductIdentifier = getOrderProductIdentifier(orderProductItem);
        if (orderProductIdentifier == null || orderProductCollectionIdentifiers.includes(orderProductIdentifier)) {
          return false;
        }
        orderProductCollectionIdentifiers.push(orderProductIdentifier);
        return true;
      });
      return [...orderProductsToAdd, ...orderProductCollection];
    }
    return orderProductCollection;
  }
}
