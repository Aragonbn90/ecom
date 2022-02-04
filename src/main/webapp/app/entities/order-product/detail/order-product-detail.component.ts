import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IOrderProduct } from '../order-product.model';

@Component({
  selector: 'jhi-order-product-detail',
  templateUrl: './order-product-detail.component.html',
})
export class OrderProductDetailComponent implements OnInit {
  orderProduct: IOrderProduct | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ orderProduct }) => {
      this.orderProduct = orderProduct;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
