import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    // Define la ruta base para la carpeta "db"
    private static final String DB_FOLDER = "db/";

    public static void main(String[] args) {
        // Crear la carpeta "db" si no existe
        createDBFolderIfNotExists();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("¿Qué acción desea realizar?");
            System.out.println("1. Dar de alta un doctor");
            System.out.println("2. Dar de alta un paciente");
            System.out.println("3. Agendar una cita");
            System.out.println("4. Salir");

            int opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1:
                    altaDoctor();
                    break;
                case 2:
                    altaPaciente();
                    break;
                case 3:
                    agendarCita();
                    break;
                case 4:
                    System.out.println("¡Hasta luego!");
                    return;
                default:
                    System.out.println("Opción no válida. Por favor, ingrese un número del 1 al 4.");
            }
        }
    }

    // Método para crear la carpeta "db" si no existe
    private static void createDBFolderIfNotExists() {
        File folder = new File(DB_FOLDER);
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                System.out.println("La carpeta 'db' ha sido creada.");
            } else {
                System.err.println("Error al crear la carpeta 'db'.");
            }
        }
    }

    // Método para dar de alta un doctor
    private static void altaDoctor() {
        List<Doctor> doctors = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Ingrese los datos del doctor:");
        System.out.print("ID del doctor: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Nombre del doctor: ");
        String nombre = scanner.nextLine();
        System.out.print("Especialidad del doctor: ");
        String especialidad = scanner.nextLine();

        doctors.add(new Doctor(id, nombre, especialidad));

        try {
            CSVWriter.writeDoctors(doctors, DB_FOLDER + "doctores.csv");
            System.out.println("El doctor ha sido dado de alta correctamente.");
        } catch (IOException e) {
            System.err.println("Error al guardar los datos: " + e.getMessage());
        }
    }

    // Método para dar de alta un paciente
    private static void altaPaciente() {
        List<Paciente> pacientes = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Ingrese los datos del paciente:");
        System.out.print("ID del paciente: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Nombre del paciente: ");
        String nombre = scanner.nextLine();

        pacientes.add(new Paciente(id, nombre));

        try {
            CSVWriter.writePacientes(pacientes, DB_FOLDER + "pacientes.csv");
            System.out.println("El paciente ha sido dado de alta correctamente.");
        } catch (IOException e) {
            System.err.println("Error al guardar los datos: " + e.getMessage());
        }
    }

    // Método para agendar una cita
    private static void agendarCita() {
        List<Cita> citas = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Ingrese los datos de la cita:");
        System.out.print("ID de la cita: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Fecha y hora de la cita (yyyy-MM-ddTHH:mm:ss): ");
        LocalDateTime fechaHora = LocalDateTime.parse(scanner.nextLine());
        System.out.print("Motivo de la cita: ");
        String motivo = scanner.nextLine();

        List<Doctor> doctors;
        try {
            doctors = CSVReader.readDoctors(DB_FOLDER + "doctores.csv");
        } catch (IOException e) {
            System.err.println("Error al cargar los datos de los doctores: " + e.getMessage());
            return;
        }

        System.out.println("Doctores disponibles:");
        for (Doctor doctor : doctors) {
            System.out.println("ID: " + doctor.getId() + ", Nombre: " + doctor.getNombre() + ", Especialidad: " + doctor.getEspecialidad());
        }
        System.out.print("ID del doctor: ");
        int doctorId = Integer.parseInt(scanner.nextLine());
        Doctor doctor = doctors.stream().filter(d -> d.getId() == doctorId).findFirst().orElse(null);
        if (doctor == null) {
            System.out.println("Doctor no encontrado.");
            return;
        }

        List<Paciente> pacientes;
        try {
            pacientes = CSVReader.readPacientes(DB_FOLDER + "pacientes.csv");
        } catch (IOException e) {
            System.err.println("Error al cargar los datos de los pacientes: " + e.getMessage());
            return;
        }

        System.out.println("Pacientes disponibles:");
        for (Paciente paciente : pacientes) {
            System.out.println("ID: " + paciente.getId() + ", Nombre: " + paciente.getNombre());
        }
        System.out.print("ID del paciente: ");
        int pacienteId = Integer.parseInt(scanner.nextLine());
        Paciente paciente = pacientes.stream().filter(p -> p.getId() == pacienteId).findFirst().orElse(null);
        if (paciente == null) {
            System.out.println("Paciente no encontrado.");
            return;
        }

        citas.add(new Cita(id, fechaHora, motivo, doctor, paciente));

        try {
            CSVWriter.writeCitas(citas, DB_FOLDER + "citas.csv");
            System.out.println("La cita ha sido agendada correctamente.");
        } catch (IOException e) {
            System.err.println("Error al guardar los datos: " + e.getMessage());
        }
    }
}

class Doctor {
    private int id;
    private String nombre;
    private String especialidad;

    public Doctor(int id, String nombre, String especialidad) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }
}

class Paciente {
    private int id;
    private String nombre;

    public Paciente(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}

class Cita {
    private int id;
    private LocalDateTime fechaHora;
    private String motivo;
    private Doctor doctor;
    private Paciente paciente;

    public Cita(int id, LocalDateTime fechaHora, String motivo, Doctor doctor, Paciente paciente) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.doctor = doctor;
        this.paciente = paciente;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Paciente getPaciente() {
        return paciente;
    }
}

class CSVReader {
    public static List<Doctor> readDoctors(String filename) throws IOException {
        List<Doctor> doctors = new ArrayList<>();
        BufferedReader csvReader = new BufferedReader(new FileReader(filename));
        String row;
        boolean headerSkipped = false;
        while ((row = csvReader.readLine()) != null) {
            if (!headerSkipped) {
                headerSkipped = true;
                continue;
            }
            String[] data = row.split(",");
            doctors.add(new Doctor(Integer.parseInt(data[0]), data[1], data[2]));
        }
        csvReader.close();
        return doctors;
    }

    public static List<Paciente> readPacientes(String filename) throws IOException {
        List<Paciente> pacientes = new ArrayList<>();
        BufferedReader csvReader = new BufferedReader(new FileReader(filename));
        String row;
        boolean headerSkipped = false;
        while ((row = csvReader.readLine()) != null) {
            if (!headerSkipped) {
                headerSkipped = true;
                continue;
            }
            String[] data = row.split(",");
            pacientes.add(new Paciente(Integer.parseInt(data[0]), data[1]));
        }
        csvReader.close();
        return pacientes;
    }
}

class CSVWriter {
    public static void writeDoctors(List<Doctor> doctors, String filename) throws IOException {
        FileWriter csvWriter = new FileWriter(filename);
        csvWriter.append("ID,Nombre,Especialidad\n");
        for (Doctor doctor : doctors) {
            csvWriter.append(String.join(",", String.valueOf(doctor.getId()), doctor.getNombre(), doctor.getEspecialidad()));
            csvWriter.append("\n");
        }
        csvWriter.flush();
        csvWriter.close();
    }

    public static void writePacientes(List<Paciente> pacientes, String filename) throws IOException {
        FileWriter csvWriter = new FileWriter(filename);
        csvWriter.append("ID,Nombre\n");
        for (Paciente paciente : pacientes) {
            csvWriter.append(String.join(",", String.valueOf(paciente.getId()), paciente.getNombre()));
            csvWriter.append("\n");
        }
        csvWriter.flush();
        csvWriter.close();
    }

    public static void writeCitas(List<Cita> citas, String filename) throws IOException {
        FileWriter csvWriter = new FileWriter(filename);
        csvWriter.append("ID,FechaHora,Motivo,DoctorID,PacienteID\n");
        for (Cita cita : citas) {
            csvWriter.append(String.join(",", String.valueOf(cita.getId()), cita.getFechaHora().toString(), cita.getMotivo(),
                    String.valueOf(cita.getDoctor().getId()), String.valueOf(cita.getPaciente().getId())));
            csvWriter.append("\n");
        }
        csvWriter.flush();
        csvWriter.close();
    }
}
