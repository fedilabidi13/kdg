import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConnectClusterComponent } from './connect-cluster.component';

describe('ConnectClusterComponent', () => {
  let component: ConnectClusterComponent;
  let fixture: ComponentFixture<ConnectClusterComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ConnectClusterComponent]
    });
    fixture = TestBed.createComponent(ConnectClusterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
