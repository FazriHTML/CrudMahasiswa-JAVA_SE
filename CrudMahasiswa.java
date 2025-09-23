import java.util.ArrayList;
import java.util.Scanner;

public class CrudMahasiswa {
    private static ArrayList<Mahasiswa> dataMahasiswa = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        tampilkanHeader();
        
        while (true) {
            tampilkanMenu();
            System.out.print("Pilih menu [1-5] : ");
            
            try {
                int pilihan = Integer.parseInt(scanner.nextLine());
                
                switch (pilihan) {
                    case 1:
                        tambahMahasiswa();
                        break;
                    case 2:
                        lihatMahasiswa();
                        break;
                    case 3:
                        updateMahasiswa();
                        break;
                    case 4:
                        hapusMahasiswa();
                        break;
                    case 5:
                        tampilkanPenutup();
                        return;
                    default:
                        tampilkanPesan("Pilihan tidak valid!", "ERROR");
                }
            } catch (NumberFormatException e) {
                tampilkanPesan("Input harus angka!", "ERROR");
            }
        }
    }
    
    private static void tampilkanHeader() {
        System.out.println("================================================================");
        System.out.println("                SISTEM CRUD MAHASISWA");
        System.out.println("                UNIVERSITAS MANDIRI JAYA");
        System.out.println("================================================================");
    }
    
    private static void tampilkanMenu() {
        System.out.println("\n=================== MENU UTAMA ===================");
        System.out.println("1. Tambah Data Mahasiswa");
        System.out.println("2. Lihat Data Mahasiswa");
        System.out.println("3. Update Data Mahasiswa");
        System.out.println("4. Hapus Data Mahasiswa");
        System.out.println("5. Keluar");
        System.out.println("==================================================");
    }
    
    // CREATE - Tambah Mahasiswa
    private static void tambahMahasiswa() {
        System.out.println("\n================ TAMBAH MAHASISWA ===============");
        
        System.out.print("NIM      : ");
        String nim = scanner.nextLine().trim();
        
        System.out.print("Nama     : ");
        String nama = scanner.nextLine().trim();
        
        System.out.print("Jurusan  : ");
        String jurusan = scanner.nextLine().trim();
        
        System.out.print("Semester : ");
        
        // Validasi input semester
        int semester = 0;
        try {
            semester = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            tampilkanPesan("Semester harus angka!", "ERROR");
            return;
        }
        
        // Validasi data
        if (nim.isEmpty() || nama.isEmpty() || jurusan.isEmpty()) {
            tampilkanPesan("Semua field harus diisi!", "ERROR");
            return;
        }
        
        if (semester < 1 || semester > 14) {
            tampilkanPesan("Semester harus antara 1-14!", "ERROR");
            return;
        }
        
        // Cek duplikasi NIM
        if (isNimExists(nim)) {
            tampilkanPesan("NIM sudah terdaftar!", "ERROR");
            return;
        }
        
        Mahasiswa mhs = new Mahasiswa(nim, nama, jurusan, semester);
        dataMahasiswa.add(mhs);
        tampilkanPesan("Mahasiswa berhasil ditambahkan!", "SUKSES");
    }
    
    // READ - Lihat Mahasiswa
    private static void lihatMahasiswa() {
        System.out.println("\n================ DATA MAHASISWA ================");
        
        if (dataMahasiswa.isEmpty()) {
            System.out.println("            Tidak ada data mahasiswa.");
        } else {
            // Header tabel
            System.out.println("+------+--------------+---------------------------+----------------------+----------+");
            System.out.println("| No.  | NIM          | Nama                      | Jurusan              | Semester |");
            System.out.println("+------+--------------+---------------------------+----------------------+----------+");
            
            // Data mahasiswa
            for (int i = 0; i < dataMahasiswa.size(); i++) {
                Mahasiswa mhs = dataMahasiswa.get(i);
                System.out.printf("| %-4d | %-12s | %-25s | %-20s | %-8d |\n",
                                (i + 1), mhs.getNim(), mhs.getNama(), 
                                mhs.getJurusan(), mhs.getSemester());
            }
            System.out.println("+------+--------------+---------------------------+----------------------+----------+");
        }
        
        System.out.println("Total mahasiswa: " + dataMahasiswa.size());
        System.out.print("\nTekan Enter untuk kembali ke menu...");
        scanner.nextLine();
    }
    
    // UPDATE - Update Mahasiswa
    private static void updateMahasiswa() {
        System.out.println("\n================ UPDATE MAHASISWA ==============");
        
        if (dataMahasiswa.isEmpty()) {
            tampilkanPesan("Tidak ada data mahasiswa untuk diupdate!", "PERINGATAN");
            return;
        }
        
        lihatMahasiswa();
        
        try {
            System.out.print("Pilih nomor mahasiswa yang akan diupdate: ");
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            
            if (index < 0 || index >= dataMahasiswa.size()) {
                tampilkanPesan("Nomor tidak valid!", "ERROR");
                return;
            }
            
            Mahasiswa mhs = dataMahasiswa.get(index);
            String nimLama = mhs.getNim();
            
            System.out.println("\nMasukkan data baru (kosongkan jika tidak ingin mengubah):");
            System.out.println("----------------------------------------------------------");
            
            System.out.print("NIM baru [" + mhs.getNim() + "]: ");
            String nim = scanner.nextLine().trim();
            if (!nim.isEmpty()) {
                if (!nim.equals(nimLama) && isNimExists(nim)) {
                    tampilkanPesan("NIM sudah terdaftar!", "ERROR");
                    return;
                }
                mhs.setNim(nim);
            }
            
            System.out.print("Nama baru [" + mhs.getNama() + "]: ");
            String nama = scanner.nextLine().trim();
            if (!nama.isEmpty()) mhs.setNama(nama);
            
            System.out.print("Jurusan baru [" + mhs.getJurusan() + "]: ");
            String jurusan = scanner.nextLine().trim();
            if (!jurusan.isEmpty()) mhs.setJurusan(jurusan);
            
            System.out.print("Semester baru [" + mhs.getSemester() + "]: ");
            String semesterStr = scanner.nextLine().trim();
            if (!semesterStr.isEmpty()) {
                try {
                    int semester = Integer.parseInt(semesterStr);
                    if (semester < 1 || semester > 14) {
                        tampilkanPesan("Semester harus antara 1-14! Data tidak diubah.", "PERINGATAN");
                    } else {
                        mhs.setSemester(semester);
                    }
                } catch (NumberFormatException e) {
                    tampilkanPesan("Semester harus angka! Data tidak diubah.", "PERINGATAN");
                }
            }
            
            tampilkanPesan("Data mahasiswa berhasil diupdate!", "SUKSES");
            
        } catch (NumberFormatException e) {
            tampilkanPesan("Input harus angka!", "ERROR");
        }
    }
    
    // DELETE - Hapus Mahasiswa
    private static void hapusMahasiswa() {
        System.out.println("\n================ HAPUS MAHASISWA ===============");
        
        if (dataMahasiswa.isEmpty()) {
            tampilkanPesan("Tidak ada data mahasiswa untuk dihapus!", "PERINGATAN");
            return;
        }
        
        lihatMahasiswa();
        
        try {
            System.out.print("Pilih nomor mahasiswa yang akan dihapus: ");
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            
            if (index < 0 || index >= dataMahasiswa.size()) {
                tampilkanPesan("Nomor tidak valid!", "ERROR");
                return;
            }
            
            Mahasiswa mhs = dataMahasiswa.get(index);
            System.out.print("Yakin ingin menghapus mahasiswa " + mhs.getNama() + "? (y/t): ");
            String konfirmasi = scanner.nextLine().trim();
            
            if (konfirmasi.equalsIgnoreCase("y") || konfirmasi.equalsIgnoreCase("ya")) {
                dataMahasiswa.remove(index);
                tampilkanPesan("Mahasiswa " + mhs.getNama() + " berhasil dihapus!", "SUKSES");
            } else {
                tampilkanPesan("Penghapusan dibatalkan.", "INFO");
            }
            
        } catch (NumberFormatException e) {
            tampilkanPesan("Input harus angka!", "ERROR");
        }
    }
    
    private static void tampilkanPenutup() {
        System.out.println("\n================================================");
        System.out.println("                TERIMA KASIH");
        System.out.println("          Sampai jumpa kembali!");
        System.out.println("================================================");
    }
    
    private static void tampilkanPesan(String message, String type) {
        String border = "================================================";
        String prefix;
        
        switch (type.toUpperCase()) {
            case "SUKSES":
                prefix = "[SUKSES] ";
                break;
            case "ERROR":
                prefix = "[ERROR] ";
                break;
            case "PERINGATAN":
                prefix = "[PERINGATAN] ";
                break;
            case "INFO":
                prefix = "[INFO] ";
                break;
            default:
                prefix = "[ ] ";
        }
        
        System.out.println("\n" + border);
        System.out.println(centerText(prefix + message, border.length()));
        System.out.println(border);
        
        // Jeda sebentar
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // Continue tanpa jeda jika thread interrupted
        }
    }
    
    private static String centerText(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        
        int padding = (width - text.length()) / 2;
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < padding; i++) {
            sb.append(" ");
        }
        
        sb.append(text);
        
        while (sb.length() < width) {
            sb.append(" ");
        }
        
        return sb.toString();
    }
    
    // Method untuk cek duplikasi NIM
    private static boolean isNimExists(String nim) {
        for (Mahasiswa mhs : dataMahasiswa) {
            if (mhs.getNim().equalsIgnoreCase(nim)) {
                return true;
            }
        }
        return false;
    }
}