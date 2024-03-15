import { Component, OnInit } from '@angular/core';
import { ApexChart, ApexXAxis, ApexStroke, ChartType } from 'ng-apexcharts';
import {
  ApexAxisChartSeries,
  ApexTitleSubtitle,
  ApexDataLabels,
} from "ng-apexcharts";
import { AuthService } from 'src/app/auth/auth.service';


@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss']
})
export class AdminDashboardComponent implements OnInit{
  constructor(private authService: AuthService){

  }
  ngOnInit(): void {
    this.authService.getClusterData().subscribe({
      next: (data) => {
        console.log(data);
        
      },
      error: (error) => {
        console.log(error.error.message)

      },
      complete: () => {
        console.log('Request completed'); // Optional
      }
    });
  }
  chartOptions2 = {
    series: [
      {
        name: "Monday",
        data: this.generateDataHeatMapWeekDays(7, 18, {
          min: 0,
          max: 90
        })
      },
      {
        name: "Tuesday",
        data: this.generateDataHeatMapWeekDays(7, 18, {
          min: 0,
          max: 90
        })
      },
      {
        name: "Wednesday",
        data: this.generateDataHeatMapWeekDays(7, 18, {
          min: 0,
          max: 90
        })
      },
      {
        name: "Thursday",
        data: this.generateDataHeatMapWeekDays(7, 18, {
          min: 0,
          max: 90
        })
      },
      {
        name: "Friday",
        data: this.generateDataHeatMapWeekDays(7, 18, {
          min: 0,
          max: 90
        })
      },
      {
        name: "Saturday",
        data: this.generateDataHeatMapWeekDays(7, 18, {
          min: 0,
          max: 90
        })
      },
      {
        name: "Sunday",
        data: this.generateDataHeatMapWeekDays(7, 18, {
          min: 0,
          max: 90
        })
      },
    ],
    chart: {
      height: 350,
      type: "heatmap" as ChartType
    },
    dataLabels: {
      enabled: false
    },
    colors: ["#008FFB"],
    xaxis: {
      categories: this.getMonths().map(month => month.name),
    },
    yaxis: {
      categories: ["00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00"],
      reversed: true,
      labels: {
        show: true
      }
    },
    title: {
      text: "Kubernetes cluster usage"
    }
  };
  
  generateDataHeatMapWeekDays(days: number, hours: number, range: { min: number, max: number }): any[] {
    const data = [];
    for (let i = 0; i < days; i++) {
      const row = [];
      for (let j = 0; j < hours; j++) {
        row.push(Math.floor(Math.random() * (range.max - range.min + 1) + range.min));
      }
      data.push(row);
    }
    return data;
  }
  
  getMonths(): { name: string, number: number }[] {
    return [
      { name: "January", number: 1 },
      { name: "February", number: 2 },
      { name: "March", number: 3 },
      { name: "April", number: 4 },
      { name: "May", number: 5 },
      { name: "June", number: 6 },
      { name: "July", number: 7 },
      { name: "August", number: 8 },
      { name: "September", number: 9 },
      { name: "October", number: 10 },
      { name: "November", number: 11 },
      { name: "December", number: 12 }
    ];
  }

  public generateDataHeatMap(count:any, yrange:any) {
    var i = 0;
    var series = [];
    while (i < count) {
      var x = "w" + (i + 1).toString();
      var y =
        Math.floor(Math.random() * (yrange.max - yrange.min + 1)) + yrange.min;

      series.push({
        x: x,
        y: y
      });
      i++;
    }
    return series;
  }
}
