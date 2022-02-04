import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { OrderProductComponent } from './list/order-product.component';
import { OrderProductDetailComponent } from './detail/order-product-detail.component';
import { OrderProductUpdateComponent } from './update/order-product-update.component';
import { OrderProductDeleteDialogComponent } from './delete/order-product-delete-dialog.component';
import { OrderProductRoutingModule } from './route/order-product-routing.module';

@NgModule({
  imports: [SharedModule, OrderProductRoutingModule],
  declarations: [OrderProductComponent, OrderProductDetailComponent, OrderProductUpdateComponent, OrderProductDeleteDialogComponent],
  entryComponents: [OrderProductDeleteDialogComponent],
})
export class OrderProductModule {}
