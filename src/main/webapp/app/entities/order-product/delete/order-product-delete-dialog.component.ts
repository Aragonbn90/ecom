import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IOrderProduct } from '../order-product.model';
import { OrderProductService } from '../service/order-product.service';

@Component({
  templateUrl: './order-product-delete-dialog.component.html',
})
export class OrderProductDeleteDialogComponent {
  orderProduct?: IOrderProduct;

  constructor(protected orderProductService: OrderProductService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.orderProductService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
