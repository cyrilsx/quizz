import { Injectable } from '@angular/core';
import { BarcodeScanner } from '@capacitor-community/barcode-scanner';
import { Platform } from '@ionic/angular';

@Injectable({
  providedIn: 'root'
})
export class QrScannerService {
  constructor(private platform: Platform) {}

  async checkPermission(): Promise<boolean> {
    return new Promise(async (resolve) => {
      const status = await BarcodeScanner.checkPermission({ force: true });
      
      if (status.granted) {
        resolve(true);
      } else if (status.denied) {
        BarcodeScanner.openAppSettings();
        resolve(false);
      } else {
        resolve(false);
      }
    });
  }

  async startScan(): Promise<string> {
    return new Promise(async (resolve, reject) => {
      try {
        const permission = await this.checkPermission();
        if (!permission) {
          reject('Camera permission not granted');
          return;
        }

        await BarcodeScanner.hideBackground();
        const result = await BarcodeScanner.startScan();

        if (result.hasContent) {
          resolve(result.content);
        } else {
          reject('No QR code found');
        }

        await BarcodeScanner.showBackground();
      } catch (error) {
        await BarcodeScanner.showBackground();
        reject(error);
      }
    });
  }

  async stopScan(): Promise<void> {
    try {
      await BarcodeScanner.stopScan();
      await BarcodeScanner.showBackground();
    } catch (error) {
      console.error('Error stopping scan:', error);
    }
  }
}