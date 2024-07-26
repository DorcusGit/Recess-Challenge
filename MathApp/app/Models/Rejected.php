<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Rejected extends Model
{
    use HasFactory;

    protected $table = 'rejecteds'; // specify the table name

    protected $primaryKey = 'rejectedID'; // specify the primary key

    protected $fillable = [
        'schoolRegNo',
        'emailAddress',
        'applicantID',
        'userName',
        'imagePath',
        'firstName',
        'lastName',
        'password',
        'dateOfBirth',
    ];

    protected $hidden = [
        'password',
    ];

    public function school()
    {
        return $this->belongsTo(School::class, 'schoolRegNo', 'schoolRegNo');
    }

    // Define other relationships or additional methods if needed
}
