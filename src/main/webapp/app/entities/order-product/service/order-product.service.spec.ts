import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IOrderProduct, OrderProduct } from '../order-product.model';

import { OrderProductService } from './order-product.service';

describe('OrderProduct Service', () => {
  let service: OrderProductService;
  let httpMock: HttpTestingController;
  let elemDefault: IOrderProduct;
  let expectedResult: IOrderProduct | IOrderProduct[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(OrderProductService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      price: 0,
      quantity: 0,
      total: 0,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a OrderProduct', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new OrderProduct()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a OrderProduct', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          price: 1,
          quantity: 1,
          total: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a OrderProduct', () => {
      const patchObject = Object.assign(
        {
          price: 1,
          total: 1,
        },
        new OrderProduct()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of OrderProduct', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          price: 1,
          quantity: 1,
          total: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a OrderProduct', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addOrderProductToCollectionIfMissing', () => {
      it('should add a OrderProduct to an empty array', () => {
        const orderProduct: IOrderProduct = { id: 123 };
        expectedResult = service.addOrderProductToCollectionIfMissing([], orderProduct);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(orderProduct);
      });

      it('should not add a OrderProduct to an array that contains it', () => {
        const orderProduct: IOrderProduct = { id: 123 };
        const orderProductCollection: IOrderProduct[] = [
          {
            ...orderProduct,
          },
          { id: 456 },
        ];
        expectedResult = service.addOrderProductToCollectionIfMissing(orderProductCollection, orderProduct);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a OrderProduct to an array that doesn't contain it", () => {
        const orderProduct: IOrderProduct = { id: 123 };
        const orderProductCollection: IOrderProduct[] = [{ id: 456 }];
        expectedResult = service.addOrderProductToCollectionIfMissing(orderProductCollection, orderProduct);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(orderProduct);
      });

      it('should add only unique OrderProduct to an array', () => {
        const orderProductArray: IOrderProduct[] = [{ id: 123 }, { id: 456 }, { id: 78526 }];
        const orderProductCollection: IOrderProduct[] = [{ id: 123 }];
        expectedResult = service.addOrderProductToCollectionIfMissing(orderProductCollection, ...orderProductArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const orderProduct: IOrderProduct = { id: 123 };
        const orderProduct2: IOrderProduct = { id: 456 };
        expectedResult = service.addOrderProductToCollectionIfMissing([], orderProduct, orderProduct2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(orderProduct);
        expect(expectedResult).toContain(orderProduct2);
      });

      it('should accept null and undefined values', () => {
        const orderProduct: IOrderProduct = { id: 123 };
        expectedResult = service.addOrderProductToCollectionIfMissing([], null, orderProduct, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(orderProduct);
      });

      it('should return initial array if no OrderProduct is added', () => {
        const orderProductCollection: IOrderProduct[] = [{ id: 123 }];
        expectedResult = service.addOrderProductToCollectionIfMissing(orderProductCollection, undefined, null);
        expect(expectedResult).toEqual(orderProductCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
