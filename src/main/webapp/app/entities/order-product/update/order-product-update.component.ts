import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IOrderProduct, OrderProduct } from '../order-product.model';
import { OrderProductService } from '../service/order-product.service';
import { IOrder } from 'app/entities/order/order.model';
import { OrderService } from 'app/entities/order/service/order.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';

@Component({
  selector: 'jhi-order-product-update',
  templateUrl: './order-product-update.component.html',
})
export class OrderProductUpdateComponent implements OnInit {
  isSaving = false;

  ordersSharedCollection: IOrder[] = [];
  productsSharedCollection: IProduct[] = [];

  editForm = this.fb.group({
    id: [],
    price: [],
    quantity: [null, [Validators.required]],
    total: [],
    order: [null, Validators.required],
    product: [null, Validators.required],
  });

  constructor(
    protected orderProductService: OrderProductService,
    protected orderService: OrderService,
    protected productService: ProductService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ orderProduct }) => {
      this.updateForm(orderProduct);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const orderProduct = this.createFromForm();
    if (orderProduct.id !== undefined) {
      this.subscribeToSaveResponse(this.orderProductService.update(orderProduct));
    } else {
      this.subscribeToSaveResponse(this.orderProductService.create(orderProduct));
    }
  }

  trackOrderById(index: number, item: IOrder): number {
    return item.id!;
  }

  trackProductById(index: number, item: IProduct): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOrderProduct>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(orderProduct: IOrderProduct): void {
    this.editForm.patchValue({
      id: orderProduct.id,
      price: orderProduct.price,
      quantity: orderProduct.quantity,
      total: orderProduct.total,
      order: orderProduct.order,
      product: orderProduct.product,
    });

    this.ordersSharedCollection = this.orderService.addOrderToCollectionIfMissing(this.ordersSharedCollection, orderProduct.order);
    this.productsSharedCollection = this.productService.addProductToCollectionIfMissing(
      this.productsSharedCollection,
      orderProduct.product
    );
  }

  protected loadRelationshipsOptions(): void {
    this.orderService
      .query()
      .pipe(map((res: HttpResponse<IOrder[]>) => res.body ?? []))
      .pipe(map((orders: IOrder[]) => this.orderService.addOrderToCollectionIfMissing(orders, this.editForm.get('order')!.value)))
      .subscribe((orders: IOrder[]) => (this.ordersSharedCollection = orders));

    this.productService
      .query()
      .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
      .pipe(
        map((products: IProduct[]) => this.productService.addProductToCollectionIfMissing(products, this.editForm.get('product')!.value))
      )
      .subscribe((products: IProduct[]) => (this.productsSharedCollection = products));
  }

  protected createFromForm(): IOrderProduct {
    return {
      ...new OrderProduct(),
      id: this.editForm.get(['id'])!.value,
      price: this.editForm.get(['price'])!.value,
      quantity: this.editForm.get(['quantity'])!.value,
      total: this.editForm.get(['total'])!.value,
      order: this.editForm.get(['order'])!.value,
      product: this.editForm.get(['product'])!.value,
    };
  }
}
