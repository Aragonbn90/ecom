import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOrderProduct, OrderProduct } from '../order-product.model';
import { OrderProductService } from '../service/order-product.service';

@Injectable({ providedIn: 'root' })
export class OrderProductRoutingResolveService implements Resolve<IOrderProduct> {
  constructor(protected service: OrderProductService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IOrderProduct> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((orderProduct: HttpResponse<OrderProduct>) => {
          if (orderProduct.body) {
            return of(orderProduct.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new OrderProduct());
  }
}
