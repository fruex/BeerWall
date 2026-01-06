using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using System.Security.Claims;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options => {
        options.Authority = "https://accounts.google.com";
        options.TokenValidationParameters = new TokenValidationParameters {
            ValidateIssuer = true,
            ValidIssuer = "https://accounts.google.com",
            ValidateAudience = true,
            ValidAudience = "220522932694-ghamgoqpqtmb0vk9ajnouiqe2h52ateb.apps.googleusercontent.com",
            ValidateLifetime = true
        };
    });

builder.Services.AddAuthorization();

// Add services to the container.
// Learn more about configuring OpenAPI at https://aka.ms/aspnet/openapi
builder.Services.AddOpenApi();

var app = builder.Build();
app.UseAuthentication();
app.UseAuthorization();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseHttpsRedirection();

var summaries = new[]
{
    "Freezing", "Bracing", "Chilly", "Cool", "Mild", "Warm", "Balmy", "Hot", "Sweltering", "Scorching"
};

app.MapGet("/weatherforecast", () =>
{
    var forecast =  Enumerable.Range(1, 5).Select(index =>
        new WeatherForecast
        (
            DateOnly.FromDateTime(DateTime.Now.AddDays(index)),
            Random.Shared.Next(-20, 55),
            summaries[Random.Shared.Next(summaries.Length)]
        ))
        .ToArray();
    return Results.Ok(new ApiEnvelope<WeatherForecast[]>(forecast));
})
.WithName("GetWeatherForecast");

var api = app.MapGroup("/api")/*.RequireAuthorization()*/;

// 1. SALDO (Balance)
api.MapGet("/balance", (ClaimsPrincipal user) => 
{
    var sampleVenueBalances = new List<VenueBalance>
    {
        new("Pub Lewe", 125.50),
        new("Browariat", 89.00),
        new("Biała Małpa", 45.25),
        new("Czarna Małpa", 250.00),
    };
    return Results.Ok(new ApiEnvelope<List<VenueBalance>>(sampleVenueBalances));
});

api.MapPost("/balance", (ClaimsPrincipal user, TopUpRequest req) =>
    Results.Ok(new ApiEnvelope<object>(new { Message = $"Doładowano kwotą {req.Amount} metodą {req.PaymentMethodId}", NewBalance = 150.50 + req.Amount })));

// 2. KARTY (Cards)
api.MapGet("/cards", (ClaimsPrincipal user) => 
{
    var sampleCards = new List<Card>
    {
        new("550e8400-e29b-41d4-a716-446655440000", "Karta Wirtualna", true, false),
        new("750e8460-e29b-41d4-a716-446655440001", "Karta fizyczna", true, true)
    };
    return Results.Ok(new ApiEnvelope<List<Card>>(sampleCards));
});

api.MapGet("/cards/{id}", (string id) => 
    Results.Ok(new ApiEnvelope<CardDetails>(new CardDetails(id, "4422", "Active", "BeerWall Classic", 1250))));

// 3. HISTORIA I MIEJSCA
api.MapGet("/history", () => 
{
    var sampleHistory = new List<Transaction>
    {
        new("1", "Pilsner Urquell", "24 lis", "19:30", -12.50, "45:32"),
        new("2", "Wino Chianti Classico", "24 lis", "20:15", -28.00, "45:32"),
        new("3", "Guinness Draught", "24 lis", "21:00", -15.00, "89:21"),
        new("4", "Corona Extra", "23 lis", "18:45", -11.00, "89:21"),
        new("5", "Prosecco", "23 lis", "19:30", -22.00, "45:32"),
        new("6", "Heineken", "23 lis", "20:15", -10.50, "45:32"),
        new("7", "Stella Artois", "23 lis", "21:30", -13.50, "89:21"),
        new("8", "Tyskie Gronie", "22 lis", "17:00", -9.50, "45:32"),
        new("9", "Wino Malbec", "22 lis", "18:30", -32.00, "89:21"),
        new("10", "IPA Craft Beer", "22 lis", "19:45", -16.00, "45:32"),
        new("11", "Żywiec Porter", "21 lis", "20:00", -14.00, "89:21"),
        new("12", "Wino Sauvignon Blanc", "21 lis", "20:45", -25.00, "45:32"),
        new("13", "Peroni Nastro Azzurro", "21 lis", "21:30", -12.00, "89:21"),
        new("14", "Desperados", "21 lis", "22:15", -11.50, "45:32"),
        new("15", "Wino Cabernet Sauvignon", "20 lis", "19:00", -30.00, "45:32"),
        new("16", "Carlsberg", "20 lis", "20:30", -10.00, "89:21"),
        new("17", "Hoegaarden", "20 lis", "21:15", -13.00, "45:32")
    };
    return Results.Ok(new ApiEnvelope<List<Transaction>>(sampleHistory));
});

api.MapGet("/places", () => 
    Results.Ok(new ApiEnvelope<List<Place>>(new List<Place> { 
        new(1, "Browar Stołeczny", 45.0), 
        new(2, "Pub pod Kuflem", 12.50) 
    })));

api.MapGet("/payment-operators", () =>
{
    var paymentOperators = new List<PaymentOperatorDto>
    {
        new("BLIK",
        [
            new(2007, "BLIK", "Płatność BLIKIEM", "https://static.sandbox.paynow.pl/payment-method-icons/2007.png",
                "ENABLED")
        ])
    };
    return Results.Ok(new ApiEnvelope<List<PaymentOperatorDto>>(paymentOperators));
});

api.MapPost("/card-activation", (CardActivationRequest req) => 
    Results.Ok(new ApiEnvelope<object>(new { CardId = req.CardId, IsActive = req.Activate, Status = "Success" })));

// 5. PROFIL (Punkty)
api.MapGet("/profile", (ClaimsPrincipal user) => {
    return Results.Ok(new ApiEnvelope<ProfileData>(new ProfileData(250)));
});

app.Run();

record WeatherForecast(DateOnly Date, int TemperatureC, string? Summary)
{
    public int TemperatureF => 32 + (int)(TemperatureC / 0.5556);
}

// --- MODELE DTO ---
record TopUpRequest(int PaymentMethodId, double Amount);
record Card(string Id, string Name, bool IsActive, bool IsPhysical);
record CardDetails(string Id, string Number, string Status, string Type, int LoyaltyPoints);
record Transaction(string Id, string BeerName, string Date, string Time, double Amount, string CardNumber);
record Place(int Id, string VenueName, double FundsAvailable);
record VenueBalance(string VenueName, double Balance);
record CardActivationRequest(string CardId, bool Activate);
record ProfileData(int LoyaltyPoints);
record PaymentMethodDto(int Id, string Name, string Description, string Image, string Status);
record PaymentOperatorDto(string Type, List<PaymentMethodDto> PaymentMethods);

record ApiEnvelope<T>(T Data);
