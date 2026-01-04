package org.fruex.beerwall.ui.models

val SampleBalances = listOf(
    LocationBalance("Pub Lewe", 125.50),
    LocationBalance("Browariat", 89.00),
    LocationBalance("Biała Małpa", 45.25)
)

val SampleCards = listOf(
    CardItem(
        id = "550e8400-e29b-41d4-a716-446655440003",
        name = "Karta Wirtualna",
        isActive = true,
        isPhysical = false
    ),
    CardItem(
        id = "750e8460-e29b-41d4-a716-446655440004",
        name = "Karta fizyczna",
        isActive = true,
        isPhysical = true
    )
)

val SampleTransactionGroups = listOf(
    TransactionGroup(
        date = "24 LISTOPADA 2025",
        transactions = listOf(
            Transaction("1", "Pilsner Urquell", "24 lis", "19:30", -12.50, "45:32"),
            Transaction("2", "Wino Chianti Classico", "24 lis", "20:15", -28.00, "45:32"),
            Transaction("3", "Guinness Draught", "24 lis", "21:00", -15.00, "89:21")
        )
    ),
    TransactionGroup(
        date = "23 LISTOPADA 2025",
        transactions = listOf(
            Transaction("4", "Corona Extra", "23 lis", "18:45", -11.00, "89:21"),
            Transaction("5", "Prosecco", "23 lis", "19:30", -22.00, "45:32"),
            Transaction("6", "Heineken", "23 lis", "20:15", -10.50, "45:32"),
            Transaction("7", "Stella Artois", "23 lis", "21:30", -13.50, "89:21")
        )
    ),
    TransactionGroup(
        date = "22 LISTOPADA 2025",
        transactions = listOf(
            Transaction("8", "Tyskie Gronie", "22 lis", "17:00", -9.50, "45:32"),
            Transaction("9", "Wino Malbec", "22 lis", "18:30", -32.00, "89:21"),
            Transaction("10", "IPA Craft Beer", "22 lis", "19:45", -16.00, "45:32")
        )
    ),
    TransactionGroup(
        date = "21 LISTOPADA 2025",
        transactions = listOf(
            Transaction("11", "Żywiec Porter", "21 lis", "20:00", -14.00, "89:21"),
            Transaction("12", "Wino Sauvignon Blanc", "21 lis", "20:45", -25.00, "45:32"),
            Transaction("13", "Peroni Nastro Azzurro", "21 lis", "21:30", -12.00, "89:21"),
            Transaction("14", "Desperados", "21 lis", "22:15", -11.50, "45:32")
        )
    ),
    TransactionGroup(
        date = "20 LISTOPADA 2025",
        transactions = listOf(
            Transaction("15", "Wino Cabernet Sauvignon", "20 lis", "19:00", -30.00, "45:32"),
            Transaction("16", "Carlsberg", "20 lis", "20:30", -10.00, "89:21"),
            Transaction("17", "Hoegaarden", "20 lis", "21:15", -13.00, "45:32")
        )
    )
)

val SampleUserProfile = UserProfile(
    name = "Waldek Waldemord",
    email = "w.w@email.com",
    initials = "WW",
    activeCards = 2,
    loyaltyPoints = 2
)
