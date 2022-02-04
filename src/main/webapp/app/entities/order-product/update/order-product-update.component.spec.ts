import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { OrderProductService } from '../service/order-product.service';
import { IOrderProduct, OrderProduct } from '../order-product.model';
import { IOrder } from 'app/entities/order/order.model';
import { OrderService } from 'app/entities/order/service/order.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';

import { OrderProductUpdateComponent } from './order-product-update.component';

describe('OrderProduct Management Update Component', () => {
  let comp: OrderProductUpdateComponent;
  let fixture: ComponentFixture<OrderProductUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let orderProductService: OrderProductService;
  let orderService: OrderService;
  let productService: ProductService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [OrderProductUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(OrderProductUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrderProductUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    orderProductService = TestBed.inject(OrderProductService);
    orderService = TestBed.inject(OrderService);
    productService = TestBed.inject(ProductService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Order query and add missing value', () => {
      const orderProduct: IOrderProduct = { id: 456 };
      const order: IOrder = { id: 85472 };
      orderProduct.order = order;

      const orderCollection: IOrder[] = [{ id: 46433 }];
      jest.spyOn(orderService, 'query').mockReturnValue(of(new HttpResponse({ body: orderCollection })));
      const additionalOrders = [order];
      const expectedCollection: IOrder[] = [...additionalOrders, ...orderCollection];
      jest.spyOn(orderService, 'addOrderToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ orderProduct });
      comp.ngOnInit();

      expect(orderService.query).toHaveBeenCalled();
      expect(orderService.addOrderToCollectionIfMissing).toHaveBeenCalledWith(orderCollection, ...additionalOrders);
      expect(comp.ordersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Product query and add missing value', () => {
      const orderProduct: IOrderProduct = { id: 456 };
      const product: IProduct = { id: 85654 };
      orderProduct.product = product;

      const productCollection: IProduct[] = [{ id: 94021 }];
      jest.spyOn(productService, 'query').mockReturnValue(of(new HttpResponse({ body: productCollection })));
      const additionalProducts = [product];
      const expectedCollection: IProduct[] = [...additionalProducts, ...productCollection];
      jest.spyOn(productService, 'addProductToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ orderProduct });
      comp.ngOnInit();

      expect(productService.query).toHaveBeenCalled();
      expect(productService.addProductToCollectionIfMissing).toHaveBeenCalledWith(productCollection, ...additionalProducts);
      expect(comp.productsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const orderProduct: IOrderProduct = { id: 456 };
      const order: IOrder = { id: 71895 };
      orderProduct.order = order;
      const product: IProduct = { id: 15278 };
      orderProduct.product = product;

      activatedRoute.data = of({ orderProduct });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(orderProduct));
      expect(comp.ordersSharedCollection).toContain(order);
      expect(comp.productsSharedCollection).toContain(product);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<OrderProduct>>();
      const orderProduct = { id: 123 };
      jest.spyOn(orderProductService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderProduct });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orderProduct }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(orderProductService.update).toHaveBeenCalledWith(orderProduct);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<OrderProduct>>();
      const orderProduct = new OrderProduct();
      jest.spyOn(orderProductService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderProduct });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orderProduct }));
      saveSubject.complete();

      // THEN
      expect(orderProductService.create).toHaveBeenCalledWith(orderProduct);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<OrderProduct>>();
      const orderProduct = { id: 123 };
      jest.spyOn(orderProductService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderProduct });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(orderProductService.update).toHaveBeenCalledWith(orderProduct);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackOrderById', () => {
      it('Should return tracked Order primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackOrderById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackProductById', () => {
      it('Should return tracked Product primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackProductById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
