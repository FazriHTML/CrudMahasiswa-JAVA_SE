import java.util.ArrayList;
import java.util.Scanner;
import java.sql.*;

public class CrudMahasiswa {
    private static ArrayList<Mahasiswa> dataMahasiswa = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        // Load data dari database saat program dimulai
        loadDataFromDatabase();
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
    
    // Method untuk load data dari database
    private static void loadDataFromDatabase() {
        String sql = "SELECT * FROM mahasiswa";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            dataMahasiswa.clear(); // Clear data existing
            
            while (rs.next()) {
                String nim = rs.getString("nim");
                String nama = rs.getString("nama");
                String jurusan = rs.getString("jurusan");
                int semester = rs.getInt("semester");
                
                Mahasiswa mhs = new Mahasiswa(nim, nama, jurusan, semester);
                dataMahasiswa.add(mhs);
            }
            
        } catch (SQLException e) {
            System.out.println("Error loading data from database: " + e.getMessage());
        }
    }
    
    private static void tampilkanHeader() {
        System.out.println("================================================================");
        System.out.println("                SISTEM CRUD MAHASISWA");
        System.out.println("                UNIVERSITAS MANDIRI");
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
        
        // Simpan ke database
        if (simpanKeDatabase(nim, nama, jurusan, semester)) {
            // Jika berhasil disimpan ke database, tambahkan ke ArrayList
            Mahasiswa mhs = new Mahasiswa(nim, nama, jurusan, semester);
            dataMahasiswa.add(mhs);
            tampilkanPesan("Mahasiswa berhasil ditambahkan!", "SUKSES");
        } else {
            tampilkanPesan("Gagal menambahkan mahasiswa ke database!", "ERROR");
        }
    }
    
    // Method untuk menyimpan ke database
    private static boolean simpanKeDatabase(String nim, String nama, String jurusan, int semester) {
        String sql = "INSERT INTO mahasiswa (nim, nama, jurusan, semester) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nim);
            pstmt.setString(2, nama);
            pstmt.setString(3, jurusan);
            pstmt.setInt(4, semester);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("Error saving to database: " + e.getMessage());
            return false;
        }
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
            
            // Update ke database
            if (updateKeDatabase(mhs, nimLama)) {
                tampilkanPesan("Data mahasiswa berhasil diupdate!", "SUKSES");
            } else {
                tampilkanPesan("Gagal mengupdate data di database!", "ERROR");
            }
            
        } catch (NumberFormatException e) {
            tampilkanPesan("Input harus angka!", "ERROR");
        }
    }
    
    // Method untuk update data di database
    private static boolean updateKeDatabase(Mahasiswa mhs, String nimLama) {
        String sql = "UPDATE mahasiswa SET nim = ?, nama = ?, jurusan = ?, semester = ? WHERE nim = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, mhs.getNim());
            pstmt.setString(2, mhs.getNama());
            pstmt.setString(3, mhs.getJurusan());
            pstmt.setInt(4, mhs.getSemester());
            pstmt.setString(5, nimLama);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("Error updating database: " + e.getMessage());
            return false;
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
                // Hapus dari database
                if (hapusDariDatabase(mhs.getNim())) {
                    dataMahasiswa.remove(index);
                    tampilkanPesan("Mahasiswa " + mhs.getNama() + " berhasil dihapus!", "SUKSES");
                } else {
                    tampilkanPesan("Gagal menghapus data dari database!", "ERROR");
                }
            } else {
                tampilkanPesan("Penghapusan dibatalkan.", "INFO");
            }
            
        } catch (NumberFormatException e) {
            tampilkanPesan("Input harus angka!", "ERROR");
        }
    }
    
    // Method untuk hapus data dari database
    private static boolean hapusDariDatabase(String nim) {
        String sql = "DELETE FROM mahasiswa WHERE nim = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nim);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("Error deleting from database: " + e.getMessage());
            return false;
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