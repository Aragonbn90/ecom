import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { OrderProductDetailComponent } from './order-product-detail.component';

describe('OrderProduct Management Detail Component', () => {
  let comp: OrderProductDetailComponent;
  let fixture: ComponentFixture<OrderProductDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OrderProductDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ orderProduct: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(OrderProductDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(OrderProductDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load orderProduct on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.orderProduct).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
