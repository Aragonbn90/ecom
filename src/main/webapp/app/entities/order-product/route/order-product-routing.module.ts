import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { OrderProductComponent } from '../list/order-product.component';
import { OrderProductDetailComponent } from '../detail/order-product-detail.component';
import { OrderProductUpdateComponent } from '../update/order-product-update.component';
import { OrderProductRoutingResolveService } from './order-product-routing-resolve.service';

const orderProductRoute: Routes = [
  {
    path: '',
    component: OrderProductComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: OrderProductDetailComponent,
    resolve: {
      orderProduct: OrderProductRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: OrderProductUpdateComponent,
    resolve: {
      orderProduct: OrderProductRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: OrderProductUpdateComponent,
    resolve: {
      orderProduct: OrderProductRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(orderProductRoute)],
  exports: [RouterModule],
})
export class OrderProductRoutingModule {}
